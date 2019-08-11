TEMPLATE_NAME=$1
STACK_NAME=$2
NETWORK_STACK_NAME=$3
KEY_NAME=$4
BUCKET_NAME=$5


if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ] || [ -z "$5" ]
  then
    echo "Error! Argument Required"
    echo "Usage - sh script.sh <TemplateFile> <Application_Stack_Name> <Network_Stack_Name> <Key_Name> <Bucket_Name>" 
    exit 1
fi

if [ ! -e $TEMPLATE_NAME ]
   then
     echo "Error! Template File does not exist"
     exit 1
fi

echo "Fetching latest AMI Image"
AMI_ID=$(aws ec2 describe-images --owners self --filter "Name=name,Values=csye6225_??????????" --output json | jq -r '.Images | sort_by(.CreationDate) | last(.[]).ImageId')
echo "Image ID : $AMI_ID "



echo "Fetching AWS ARN for SSL Certificate"
CERTIFICATE_ARN=$(aws acm list-certificates --certificate-statuses ISSUED --query "CertificateSummaryList[?DomainName=='csye6225-su19-$BUCKET_NAME.me']"  | jq -r ".[0].CertificateArn")
echo "CertificateArn : $CERTIFICATE_ARN"

echo "Creating Auto-Scaling Application Stack"
STACK_ID=$(\
aws cloudformation create-stack --stack-name ${STACK_NAME} \
--template-url https://s3.amazonaws.com/code-deploy.csye6225-su19-${BUCKET_NAME}.me/csye6225-cf-auto-scaling-application.json \
--parameters ParameterKey=StackName,ParameterValue=${STACK_NAME} \
ParameterKey=NetworkStackName,ParameterValue=${NETWORK_STACK_NAME} \
ParameterKey=AmiId,ParameterValue=${AMI_ID} \
ParameterKey=KeyName,ParameterValue=${KEY_NAME} \
ParameterKey=BucketName,ParameterValue=${BUCKET_NAME} \
ParameterKey=CertificateArn,ParameterValue=${CERTIFICATE_ARN} \
--capabilities CAPABILITY_NAMED_IAM \
| jq -r .StackId \
)

echo "Waiting on ${STACK_ID} create completion..."
aws cloudformation wait stack-create-complete --stack-name ${STACK_ID}
echo "Status of create Stack is as below :"
aws cloudformation describe-stacks --stack-name ${STACK_ID} | jq .Stacks[0].StackStatus

