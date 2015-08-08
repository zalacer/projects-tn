
20150802

flaskdemo project

This project requires Python3. Python3.4.3 was used for testing.

It is configured according to
    http://flask.pocoo.org/docs/0.10/patterns/packages/
    
This configuration has the effect of forcing __name__
to resolve to package name flaskdemo in the definition of
app in flaskdemo/__init__.py even when runserver.py is 
executed from outside the outer flaskdemo containing directory.

20150808

Added demo of reading configuration file:
    configuration file is flaskdemo/config.py
    and it is read by flaskdemo/views.py
