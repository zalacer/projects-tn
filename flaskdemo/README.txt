
flaskdemo project is configured according to
    http://flask.pocoo.org/docs/0.10/patterns/packages/
    
This configuration has the effect of forcing __name__
to resolve to package name flaskdemo in the definition of
app in flaskdemo/__init__.py even when runserver.py is 
executed from outside the outer flaskdemo containing directory.
