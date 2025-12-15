---bash to Activate SQLite Database and Create Tables
'''
cd ~/jade
sqlite3 anomalies.db
'''


-- Create anomalies table
CREATE TABLE IF NOT EXISTS anomalies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    node_id TEXT NOT NULL,
    anomaly_type TEXT NOT NULL,
    value REAL,
    threshold REAL,
    status TEXT,
    details TEXT
);

-- Create audit_reports table
CREATE TABLE IF NOT EXISTS audit_reports (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    target_node TEXT NOT NULL,
    cpu_usage REAL,
    memory_usage TEXT,
    top_process TEXT,
    recommendation TEXT,
    full_report TEXT
);

-- Create agent_actions table
CREATE TABLE IF NOT EXISTS agent_actions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    agent_name TEXT NOT NULL,
    action_type TEXT NOT NULL,
    target_node TEXT,
    result TEXT
);

-- Verify tables created
.tables

-- Exit SQLite
.exit
```

**Expected output:**
```
agent_actions  anomalies  audit_reports