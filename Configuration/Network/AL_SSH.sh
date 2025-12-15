# 1. Update package lists
apt-get update

# 2. Install SSH server
apt-get install -y openssh-server

# 3. Set root password
passwd root
# Enter password: jade123
# Confirm: jade123

# 4. Configure SSH to allow root login
sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
sed -i 's/PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config

# Also add this explicitly
echo "PermitRootLogin yes" >> /etc/ssh/sshd_config

# 5. Create SSH directory (if not exists)
mkdir -p /var/run/sshd
mkdir -p /root/.ssh

# 6. Start SSH service
service ssh start

# 7. Verify SSH is running
service ssh status
# Should show: "sshd is running"

# 8. Check if port 22 is listening
netstat -tlnp | grep 22
# OR
ss -tlnp | grep 22
# Should show: "LISTEN ... :22"