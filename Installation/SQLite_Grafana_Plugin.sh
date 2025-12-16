#Check the Plugin Directory
#https://github.com/fr-ser/grafana-sqlite-datasource?tab=readme-ov-file#plugin-installation

sudo grafana-cli plugins install frser-sqlite-datasource
# For systems using systemd (common on modern Ubuntu)
sudo systemctl restart grafana-server