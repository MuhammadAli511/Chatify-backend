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
    name = request.form["name"]
    email = request.form["email"]
    password = request.form["password"]
    gender = request.form["gender"]
    phone = request.form["phone"]
    profileUrl = request.form["profileUrl"]
    userStatus = request.form["userStatus"]
    deviceID = request.form["deviceID"]
    

    # Checking if email already exists    
    cursor.execute("SELECT * FROM users WHERE email = %s", (email,))
    record = cursor.fetchone()
    if record:
        return jsonify({"message": "Email already exists"})
    
    # Inserting data into database
    cursor.execute("INSERT INTO users (name, email, password, gender, phoneNum, profileUrl, userStatus, deviceID) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)", (name, email, password, gender, phone, profileUrl, userStatus, deviceID))
    connection.commit()
    if cursor.rowcount == 1:
        return jsonify({"message": "User created successfully"})
    else:
        return jsonify({"message": "Error creating user"})


@app.route("/signin", methods=["POST"])
def signin():
    email = request.form["email"]
    password = request.form["password"]
    deviceID = request.form["deviceID"]

    # Checking if email already exists    
    cursor.execute("SELECT * FROM users WHERE email = %s AND password = %s", (email, password))
    record = cursor.fetchone()
    if record:
        cursor.execute("UPDATE users SET deviceID = %s WHERE email = %s", (deviceID, email))
        connection.commit()
        return jsonify({"message": "User signed in successfully"})
    else:
        return jsonify({"message": "Invalid credentials"})



if __name__ == "__main__":
    print(index())
    app.run(debug=True)