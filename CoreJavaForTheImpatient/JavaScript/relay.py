import sys

l = ''
for line in sys.stdin:
  l = line.strip()
  
x = l.split()
if (len(x)==0): print(l + ' ' + 'none')
if (len(x)==1): print(l + ' ' + 'and')
if (len(x)==2): print(l + ' ' + 'one')
if (len(x)==3): print(l + ' ' + 'is')
if (len(x)==4): print(l + ' ' + 'two')

        

    