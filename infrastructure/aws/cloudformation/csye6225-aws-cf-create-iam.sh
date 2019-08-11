TEMPLATE_NAME=$1
STACK_NAME=$2
BUCKET_NAME=$3


if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ]
  then
    echo "Error! Argument Required"
    echo "Usage - sh script.sh <TemplateFile> <Stack_Name> <Code_Deploy_S3_Bucket_Name>" 
    exit 1
fi

if [ ! -e $TEMPLATE_NAME ]
   then
     echo "Error! Template File does not exist"
     exit 1
fi

echo "Fetching Account ID from AWS CLI"
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --output text --query Account)
echo "ACCOUNT_ID : $AWS_ACCOUNT_ID "

echo "Fetching Region from AWS CLI"
AWS_REGION=$(aws configure get region)
echo "REGION : $AWS_REGION "


echo "Creating Application Stack"
STACK_ID=$(\
aws cloudformation create-stack --stack-name ${STACK_NAME} \
--template-body file://${TEMPLATE_NAME} \
--parameters ParameterKey=StackName,ParameterValue=${STACK_NAME} \
ParameterKey=AWSRegion,ParameterValue=${AWS_REGION} \
ParameterKey=AWSAccountID,ParameterValue=${AWS_ACCOUNT_ID} \
ParameterKey=BucketName,ParameterValue=${BUCKET_NAME} \
--capabilities CAPABILITY_NAMED_IAM \
| jq -r .StackId \
)

echo "Waiting on ${STACK_ID} create completion..."
aws cloudformation wait stack-create-complete --stack-name ${STACK_ID}
echo "Status of create Stack is as below :"
aws cloudformation describe-stacks --stack-name ${STACK_ID} | jq .Stacks[0].StackStatus

