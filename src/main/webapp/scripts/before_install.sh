sudo yum update
sudo yum install ruby
sudo yum install wget
cd /home/ec2-user
wget https://aws-codedeploy-us-east-1.s3.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto
cd /var/liv/tomcat/webapps
cp /home/ec2-user/PZDemoApp.war /var/lib/tomcat/webapps