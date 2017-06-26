
db.createUser(
    {
        user: "db",
        pwd: "dbpass",
        roles: [
            {role: "readWrite", db: "dashboard"}
        ]
    })
db.dummmyCollection.insert({x: 1});
