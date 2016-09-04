use
dashboarddb
db.createUser(
    {
        user: "dashboarduser",
        pwd: "1qazxSw2",
        roles: [
            {role: "readWrite", db: "dashboard"}
        ]
    })
db.dummmyCollection.insert({x: 1});