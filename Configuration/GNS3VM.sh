#Check the Option Menu, it contains: gns3@IP_Address + Password
ssh gns3@IP_Address
#Password: gns3

# Limit LA1 to a max of 0.5 CPU core
docker update --cpus 0.5 3b716506ef18

# Limit LA2 to a max of 0.5 CPU core
docker update --cpus 0.5 58e4ed58222a