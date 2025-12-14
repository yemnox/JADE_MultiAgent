#Use this guide to install VMware Workstation Pro on Linux systems
#https://www.configserverfirewall.com/ubuntu-linux/install-vmware-workstation-ubuntu-24/
#Youtube video: https://www.youtube.com/watch?v=Y_DMPklgFnY

#Install required packages
sudo apt update
sudo apt install build-essential dkms linux-headers-$(uname -r)

#Run the Installer
cd ~/Downloads
sudo sh VMware-Workstation-Full-*.bundle