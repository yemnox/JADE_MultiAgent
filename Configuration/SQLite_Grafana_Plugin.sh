#Check the Plugin Directory
#https://github.com/fr-ser/grafana-sqlite-datasource?tab=readme-ov-file#plugin-installation

sudo grafana-cli plugins install frser-sqlite-datasource
# For systems using systemd (common on modern Ubuntu)
sudo systemctl restart grafana-server


#////////////GRAfFANA SQLITE DB ACCESS PERMISSIONS ////////////
# 1. Grant read access to the database file
sudo chmod a+r /home/yemnox/jade/anomalies.db

# 2. Ensure the Grafana user can traverse the directory structure
# This command grants execute/search permission to others on the directory.
sudo chmod o+x /home/yemnox/
sudo chmod o+x /home/yemnox/jade/