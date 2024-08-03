--list of commands

* AddGroup <GroupName> (Creates a new group)
* JoinGroup <GroupName> (Joins the group, displays an error message if group does not exist or if user is already in the group)
* SendMessage <GroupName> <Message> (Sends a message to the group, displays an error message if group does not exist
  or if user is not in the group )
* LeaveGroup <GroupName> (Leaves the group, displays an error message if group does not exist)
* RemoveGroup <GroupName> (Removes the group, displays an error message if group does not exist)
* SendFile <GroupName> <File> (Sends a file to the group)
* AddUserToGroup <GroupName> <UserName> (Adds a user to the group, displays an error message if group or user does not exist or if user is not in the group )
* PrivateMessage <UserName> <Message> (Sends a private message to the user, displays an error message if user does not exist)


--project run description

//build
* mvn clean package

//execute project

--server
* mvn spring-boot:run

--client (execute on multiple terminals for multiple users)
* java -cp target/classes org.example.whatsappchat.client.ChatClient


--EXECUTE GOOGLE CLOUD COMMANDS (Only create group and join group are implemented in cloud, all the other methods correctly work locally)

* Make HTTP Requests to https://whatsappchatfinalproject.lm.r.appspot.com/

* api/create-group
  Method: POST
  Params: groupName = testGroup

* api/join-group
  Method: POST
  Params: groupName = testGroup