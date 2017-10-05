use dashboard;

db.createUser({  
  user: "db",
  pwd: "dbpass",
  "roles": [
    {
      "role": "readWrite",
      "db": "dashboard"
    }
  ]
});