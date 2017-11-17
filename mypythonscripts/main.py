from flask import Flask, jsonify,Response
import literadar
import json
app = Flask(__name__)

tasks = [
    {
        'id': 1,
        'title': u'Buy groceries',
        'description': u'Milk, Cheese, Pizza, Fruit, Tylenol', 
        'done': False
    },
    {
        'id': 2,
        'title': u'Learn Python',
        'description': u'Need to find a good Python tutorial on the web', 
        'done': False
    }
]

@app.route('/tasks', methods=['GET'])
def get_tasks():
    return jsonify({'tasks': tasks})

@app.route('/apk', methods=['GET'])
def getapk():
    return jsonify(literadar.run('app2.apk'))
    #return jsonify(literadar.run('org.adaway_60.apk'))


if __name__ == '__main__':
    #app.run(debug=True)
    app.run(host='0.0.0.0', port=5000,debug=True)
