import mysql.connector
from flask import Flask, request, jsonify
from mysql.connector import Error, errorcode
from flask_cors import CORS


app = Flask(__name__)

try:
    connection = mysql.connector.connect(host='localhost', database='smd', user='root', password='1234')
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
    cursor = connection.cursor()  
    cursor.execute("SELECT * FROM users WHERE email = %s", (email,))
    record = cursor.fetchone()
    if record:
        cursor.close()
        return jsonify({"message": "Email already exists"})

    # Checking if phone already exists
    cursor.execute("SELECT * FROM users WHERE phoneNum = %s", (phone,))
    record = cursor.fetchone()
    if record:
        cursor.close()
        return jsonify({"message": "Phone already exists"})
    
    # Inserting data into database
    cursor.execute("INSERT INTO users (name, email, password, gender, phoneNum, profileUrl, userStatus, deviceID) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)", (name, email, password, gender, phone, profileUrl, userStatus, deviceID))
    connection.commit()
    if cursor.rowcount == 1:
        cursor.close()
        return jsonify({"message": "User created successfully"})
    else:
        cursor.close()
        return jsonify({"message": "Error creating user"})


@app.route("/signin", methods=["POST"])
def signin():
    email = request.form["email"]
    password = request.form["password"]
    deviceID = request.form["deviceID"]

    # Checking if email already exists    
    cursor = connection.cursor()
    cursor.execute("SELECT * FROM users WHERE email = %s AND password = %s", (email, password))
    record = cursor.fetchone()
    if record:
        cursor.execute("UPDATE users SET deviceID = %s WHERE email = %s", (deviceID, email))
        connection.commit()
        cursor.close()
        return jsonify({"message": "User signed in successfully"})
    else:
        cursor.close()
        return jsonify({"message": "Invalid credentials"})



@app.route("/updatePass", methods=["POST"])
def updatePass():
    email = request.form["email"]
    password = request.form["password"]

    # Checking if email already exists
    cursor = connection.cursor()
    cursor.execute("SELECT * FROM users WHERE email = %s", (email,))
    record = cursor.fetchone()
    if record:
        cursor.execute("UPDATE users SET password = %s WHERE email = %s", (password, email))
        connection.commit()
        cursor.close()
        return jsonify({"message": "done"})
    else:
        cursor.close()
        return jsonify({"message": "Invalid credentials"})





@app.route("/getProfilePic", methods=["POST"])
def getProfilePic():
    email = request.form["email"]
    
    cursor = connection.cursor()
    cursor.execute("SELECT profileUrl, name FROM users WHERE email = %s", (email,))
    record = cursor.fetchone()
    cursor.close()
    if record:
        return jsonify({"profileUrl": record[0], "name": record[1]})
    else:
        return jsonify({"message": "Error fetching profile pic"})


@app.route("/getNamePic", methods=["POST"])
def getNamePic():
    email = request.form["email"]
    
    cursor = connection.cursor()
    cursor.execute("SELECT name, profileUrl FROM users WHERE email = %s", (email,))
    record = cursor.fetchone()
    cursor.close()
    if record:
        return jsonify({"name": record[0], "profileUrl": record[1]})
    else:
        return jsonify({"message": "Error fetching profile pic and name"})

    

@app.route("/sendMsg", methods=["POST"])
def sendMsg():
    message = request.form["message"]
    receiver = request.form["receiver"]
    sender = request.form["sender"]
    timestamp = request.form["timestamp"]
    messageType = request.form["messageType"]

    cursor = connection.cursor()
    cursor.execute("INSERT INTO chat (message, receiver, sender, timestamp, messageType) VALUES (%s, %s, %s, %s, %s)", (message, receiver, sender, timestamp, messageType))
    connection.commit()

    if cursor.rowcount == 1:
        cursor.close()
        cursor = connection.cursor()
        cursor.execute("SELECT deviceID FROM users WHERE email = %s", (receiver,))
        record = cursor.fetchone()
        cursor.close()
        if record:
            return jsonify({"deviceID": record[0]})
    else:
        cursor.close()
        return jsonify({"message": "Error sending message"})



@app.route("/getLastMsg", methods=["POST"])
def getLastMsg():
    receiver = request.form["receiver"]
    sender = request.form["sender"]

    cursor = connection.cursor()
    cursor.execute("SELECT * FROM chat WHERE receiver = %s AND sender = %s OR receiver = %s AND sender = %s ORDER BY timestamp DESC LIMIT 1", (receiver, sender, sender, receiver))
    record = cursor.fetchone()
    cursor.close()

    if record:
        return jsonify({"message": record[1], "messageType": record[5]})
    else:
        return jsonify({"message": "No messages found"})




@app.route("/getMsg", methods=["POST"])
def getMsg():
    sender = request.form["sender"]
    receiver = request.form["receiver"]

    cursor = connection.cursor()
    cursor.execute("SELECT * FROM chat WHERE (sender = %s AND receiver = %s) OR (sender = %s AND receiver = %s)", (sender, receiver, receiver, sender))
    record = cursor.fetchall()
    cursor.close()
    if record:
        return jsonify({"message": record})
    else:
        return jsonify({"message": "Error fetching message"})


@app.route("/deleteMsg", methods=["POST"])
def deleteMsg():
    id = request.form["id"]

    cursor = connection.cursor()
    cursor.execute("DELETE FROM chat WHERE id = %s", (id,))
    connection.commit()

    if cursor.rowcount == 1:
        cursor.close()
        return jsonify({"message": "Message deleted successfully"})
    else:
        cursor.close()
        return jsonify({"message": "Error deleting message"})


@app.route("/updateMsg", methods=["POST"])
def updateMsg():
    id = request.form["id"]
    message = request.form["message"]

    cursor = connection.cursor()
    cursor.execute("UPDATE chat SET message = %s WHERE id = %s", (message, id))
    connection.commit()

    if cursor.rowcount == 1:
        cursor.close()
        return jsonify({"message": "Message updated successfully"})
    else:
        cursor.close()
        return jsonify({"message": "Error updating message"})




@app.route("/getContacts", methods=["POST"])
def getContacts():
    email = request.form["email"]
    # Get id with email
    cursor = connection.cursor()
    cursor.execute("SELECT userID FROM users WHERE email = %s", (email,))
    record = cursor.fetchone()
    if record:
        id = record[0]
        id = record[0]
        # create a list of contacts
        contacts = []
        # Get contacts with id
        cursor.execute("SELECT contactID FROM contacts WHERE userID = %s", (id,))
        records = cursor.fetchall()
        for record in records:
            contactID = record[0]
            cursor.execute("SELECT * FROM users WHERE userID = %s", (contactID,))
            contact = cursor.fetchone()
            contacts.append(contact)
        cursor.close()
        return jsonify({"contacts": contacts})
        
        
    else:
        cursor.close()
        return jsonify({"message": "Invalid Email"})



@app.route("/getStatus", methods=["POST"])
def getStatus():
    receiver = request.form["email"]

    cursor = connection.cursor()
    cursor.execute("SELECT userStatus FROM users WHERE email = %s", (receiver,))
    record = cursor.fetchone()
    cursor.close()
    if record:
        return jsonify({"userStatus": record[0]})
    else:
        return jsonify({"message": "Error fetching status"})


@app.route("/updateStatus", methods=["POST"])
def updateStatus():
    email = request.form["email"]
    status = request.form["status"]

    cursor = connection.cursor()
    cursor.execute("UPDATE users SET userStatus = %s WHERE email = %s", (status, email))
    connection.commit()
    cursor.close()
    return jsonify({"message": "Status updated successfully"})










@app.route("/addContact", methods=["POST"])
def addContact():
    email = request.form["email"]
    phoneNum = request.form["phoneNum"]

    # Get id with email
    cursor = connection.cursor()
    cursor.execute("SELECT userID FROM users WHERE email = %s", (email,))
    record = cursor.fetchone()
    if record:
        id = record[0]
        # Get id with phoneNum
        cursor.execute("SELECT userID FROM users WHERE phoneNum = %s", (phoneNum,))
        record = cursor.fetchone()
        if record:
            contactID = record[0]
            # Check if contact already exists
            cursor.execute("SELECT * FROM contacts WHERE userID = %s AND contactID = %s", (id, contactID))
            record = cursor.fetchone()
            if record:
                cursor.close()
                return jsonify({"message": "Contact Already Exists"})


            cursor.execute("INSERT INTO contacts (userID, contactID) VALUES (%s, %s)", (id, contactID))
            connection.commit()
            if cursor.rowcount == 1:
                # Inserting data into database
                cursor.execute("INSERT INTO contacts (userID, contactID) VALUES (%s, %s)", (contactID, id))
                connection.commit()
                if cursor.rowcount == 1:
                    cursor.close()
                    return jsonify({"message": "Contact Added successfully"})
                else:
                    cursor.close()
                    return jsonify({"message": "Error adding contact"})
            else:
                cursor.close()
                return jsonify({"message": "Error adding contact"})
    else:
        cursor.close()
        return jsonify({"message": "Invalid Contact Number"})


if __name__ == "__main__":
    print(index())
    app.run(debug=True)