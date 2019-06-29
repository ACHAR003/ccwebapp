TEMPLATE_NAME=$1
AMI_ID=$2
STACK_NAME=$3
NETWORK_STACK_NAME=$4
BUCKET_NAME=$5
KEY_NAME=$6


if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ] || [ -z "$5" ] || [ -z "$6" ]
  then
    echo "Error! Argument Required"
    echo "Usage - sh script.sh <TemplateFile> <AMI_ID> <Application_Stack_Name> <Network_Stack_Name> <S3_Bucket_Name> <Key_Name>" 
    exit 1
fi

if [ ! -e $TEMPLATE_NAME ]
   then
     echo "Error! Template File does not exist"
     exit 1
fi

echo "Creating Application Stack"
STACK_ID=$(\
aws cloudformation create-stack --stack-name ${STACK_NAME} \
--template-body file://${TEMPLATE_NAME} \
--parameters ParameterKey=StackName,ParameterValue=${STACK_NAME} \
ParameterKey=NetworkStackName,ParameterValue=${NETWORK_STACK_NAME} \
ParameterKey=AmiId,ParameterValue=${AMI_ID} \
ParameterKey=BucketName,ParameterValue=${BUCKET_NAME} \
ParameterKey=KeyName,ParameterValue=${KEY_NAME} \
--capabilities CAPABILITY_NAMED_IAM \
| jq -r .StackId \
)

echo "Waiting on ${STACK_ID} create completion..."
aws cloudformation wait stack-create-complete --stack-name ${STACK_ID}
echo "Status of create Stack is as below :"
aws cloudformation describe-stacks --stack-name ${STACK_ID} | jq .Stacks[0].StackStatus

