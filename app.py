from flask import Flask, jsonify
import mysql.connector

app = Flask(__name__)

def get_connection():
    return mysql.connector.connect(
        host="localhost",        # ou ton IP de base
        user="root",             # ton utilisateur MySQL
        password="",             # ton mot de passe (laisse vide si aucun)
        database="pidev"  # 🔁 adapte ici
    )

@app.route("/")
def home():
    return "✅ API MySQL en ligne"

@app.route("/api/ratings/<int:article_id>")
def get_ratings(article_id):
    conn = get_connection()
    cursor = conn.cursor()

    query = "SELECT rating, COUNT(*) FROM comment WHERE article_id = %s GROUP BY rating"
    cursor.execute(query, (article_id,))
    
    stats = {str(i): 0 for i in range(1, 6)}
    for rating, count in cursor.fetchall():
        stats[str(rating)] = count

    conn.close()

    return jsonify({"details": stats})
