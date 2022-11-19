import mysql.connector
from flask import Flask, request, jsonify
from mysql.connector import Error, errorcode
from flask_cors import CORS


app = Flask(__name__)

try:
    connection = mysql.connector.connect(host='localhost', database='smd', user='root', password='1234')
    cursor = connection.cursor()
except Error as e:
    error = "message: Error while connecting to MySQL error : " + e
    print(error)


@app.route("/")
def index():
    return "Server Health: Good"


@app.route("/signup", methods=["POST"])
def signup():
    data = request.get_json()

    name = data["name"]
    email = data["email"]
    password = data["password"]
    gender = data["gender"]
    phone = data["phone"]
    profileUrl = data["profileUrl"]
    userStatus = data["userStatus"]
    

    # Checking if email already exists    
    cursor.execute("SELECT * FROM users WHERE email = %s", (email,))
    record = cursor.fetchone()
    if record:
        return jsonify({"message": "Email already exists"})
    
    # Inserting data into database
    cursor.execute("INSERT INTO users (name, email, password, gender, phoneNum, profileUrl, userStatus) VALUES (%s, %s, %s, %s, %s)", (name, email, password, gender, phone, profileUrl, userStatus))
    connection.commit()
    return jsonify({"message": "Account created successfully"})



if __name__ == "__main__":
    print(index())
    app.run(debug=True)