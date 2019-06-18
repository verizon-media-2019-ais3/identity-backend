# Test POST method for createAccount
curl -d'{"username":"Alice","firstName":"Alice","lastName":"Demo","email":"alice@gmail.com", "verified":true, "createTime":"2017-07-21T17:32:28Z","updateTime":"2017-07-21T17:32:28Z","description":"Test account for Alice", "password":"PASSWORD", "blockUntil":"2017-07-21T17:32:28Z", "nthTrial":0}' -H "Content-Type: application/json" -X POST http://localhost:8080/v1/accounts/ -i
echo ""
curl -d'{"username":"Bob","firstName":"Bob","lastName":"Demo","email":"bob@gmail.com", "verified":true, "createTime":"2017-07-21T17:32:28Z","updateTime":"2017-07-21T17:32:28Z","description":"Test account for Bob", "password":"PASSWORD", "blockUntil":"2017-07-21T17:32:28Z", "nthTrial":0}' -H "Content-Type: application/json" -X POST http://localhost:8080/v1/accounts/ -i
echo ""

# TEST GET method for getAccount
curl http://localhost:8080/v1/accounts/Alice
echo ""
curl http://localhost:8080/v1/accounts/Bob
echo ""

# Test POST method for login session
curl -d'{"username":"Alice","password":"PASSWORD"}' -H "Content-Type: application/json" -X POST http://localhost:8080/v1/sessions/ -i
echo ""

# TEST GET method for private profile
curl -X GET http://localhost:8080/v1/accounts/@me?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBbGljZSIsImV4cCI6MTU2MTUwMTExMCwiaWF0IjoxNTYwODk2MzEwfQ.h-TvCTMyN6LvDMwHGETcGDuYxp2E2GwURoUNDBSKi9o
echo ""

# TEST PUT method for updateAccount
# curl -d'{"username":"Alice","email":"notAlice@gmail.com", "verified":true, "updateTime":"2018-01-01T17:32:28Z","description": "Info changed for Alice", "password":"QWERTY", "blockUntil":"2018-01-01T17:54:28Z", "nthTrial":1}' -H "Content-Type: application/json" -X PUT http://localhost:8080/v1/accounts/@me
# echo ""

# Test GET method after update
# curl http://localhost:8080/v1/accounts/Bob
# echo ""
