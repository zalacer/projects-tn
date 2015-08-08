
from flask import Flask, request, render_template
from flaskdemo import app
from flaskdemo import config

app.config['SQLALCHEMY_DATABASE_URI'] = config.database_uri
print("app.config['SQLALCHEMY_DATABASE_URI'] =", app.config['SQLALCHEMY_DATABASE_URI'])

@app.route('/greeting/')
def echo():
  kwargs = {}
  kwargs['person'] = request.args.get('person')
  kwargs['place'] = request.args.get('place')
  return render_template('flaskdemo.html', **kwargs)
  