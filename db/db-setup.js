use dashboarddb;

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