if [ -z "$1" ]
then
        echo "Please provide a valid command line argument for stack_name"
        exit 1
else
        echo "Initiating with deletion of stack using cloudformation"
fi

STACK_NAME=$(aws cloudformation describe-stacks --stack-name "$1" --query "Stacks[0].StackId" --output text )

if [ $? -eq 0 ]
    then
        aws cloudformation delete-stack --stack-name $STACK_NAME
        aws cloudformation wait stack-delete-complete --stack-name $STACK_NAME
        aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[*].StackStatus" --output text

   if [ $? -eq 0 ]
        then
            echo "Stack $1 successfully deleted!!!"
   else
            echo "Failed Stack deletion"
            exit 1
   fi



else
        echo "Stack $1 doesn't exist"
        exit 0
fi

