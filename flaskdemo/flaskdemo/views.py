
from flask import Flask, request, render_template
from flaskdemo import app

@app.route('/greeting/')
def echo():
  kwargs = {}
  kwargs['person'] = request.args.get('person')
  kwargs['place'] = request.args.get('place')
  return render_template('flaskdemo.html', **kwargs)
  