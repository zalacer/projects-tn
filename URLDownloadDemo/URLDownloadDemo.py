
# http://stackoverflow.com/questions/31669040/i-get-timeouterror-winerror-10060-when-i-parsing-my-school-website
# run chcp 65001 in cmd.exe window before running this script

import requests

url='http://jakjeon.icems.kr/main.do'
r = requests.get(url)
print(r.status_code)
print(r.headers['content-type'])
print(r.encoding)
print(r.text)



