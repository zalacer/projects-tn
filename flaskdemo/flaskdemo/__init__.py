from flask import Flask

print("__init__.py: __name__ =", __name__)

#app = Flask('flaskdemo') # ok, helpers.py self.root_path=C:\Users\tn\Documents\python\A\flaskdemo\flaskdemo
app = Flask(__name__) # ok, __name__ resolves to 'flaskdemo' 
                      # helpers.py self.root_path=C:\Users\tn\Documents\python\A\flaskdemo\flaskdemo
#app = Flask('xxx') # jinja2.exceptions.TemplateNotFound: flaskdemo.html
#app = Flask() # TypeError: __init__() missing 1 required positional argument: 'import_name'

import flaskdemo.views

