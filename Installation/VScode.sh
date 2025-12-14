#Official Documentation: https://code.visualstudio.com/docs/setup/linux

#Download Deb Package
cd ~Downloads
sudo apt install ./<file>.deb #Check File Name in the Downloads Folder
echo "code code/add-microsoft-repo boolean true" | sudo debconf-set-selections