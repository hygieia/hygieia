print("Creating database user")
db.createUser({user: "db", pwd: "password", roles: [{role: "readWrite", db: "dashboard"}]});
print("User db created")

print("Inserting test user account into Hygieia Database...")
db.authentication.insert({ "_class" : "com.capitalone.dashboard.model.Authentication", "username" : "hygieia_test_user", "password" : "sha512:b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f1326af5a2ea6d103fd07c95385ffab0cacbc86" })
print("User hygieia_test_user account inserted")
