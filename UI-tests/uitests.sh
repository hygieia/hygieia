export MONGO_IMAGE=[ mongo image ]
export API_IMAGE=[ api image ]
export UI_IMAGE=[ ui image ]
export HUB_IMAGE=[ hub image ] 
export NODE1_IMAGE=[ browser node image ]
export NODE1_DRIVER=[ browser driver name ]
export TEST_IMAGE=[ ui test image ]

export SSL=[ true | false ]

BASE_URL_ARG='http://hygieia-ui:80'
if [ "$SSL" = true ] ; then
    BASE_URL_ARG='https://hygieia-ui:443'
fi

docker-compose rm -f
docker-compose build
docker-compose up --timeout 1 --no-build -d

docker-compose run uitests mvn clean verify -f UI-tests/pom.xml -DUITEST_EXISTING_USERNAME=hygieia_test_user -DUITEST_EXISTING_PASSWORD=password -Dwebdriver.base.url=$BASE_URL_ARG -Dwebdriver.remote.url=http://hub:4444/wd/hub -Dwebdriver.remote.driver=$NODE1_DRIVER

docker-compose stop
docker-compose down
