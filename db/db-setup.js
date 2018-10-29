db = db.getSiblingDB("dashboarddb");

db.createUser({
  user: "dashboarduser",
  pwd: "dbpassword",
  "roles": [
    {
      "role": "readWrite",
      "db": "dashboarddb"
    }
  ]
});