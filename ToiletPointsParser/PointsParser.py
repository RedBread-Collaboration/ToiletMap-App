from turtle import title
from requests import get
from bs4 import BeautifulSoup as bs
import re
from yandex_geocoder import Client
from yandex_geocoder.exceptions import NothingFound
import YaMapsTools


site = get('https://skr.su/news/18/2019-02-26/nuzhnyy-putevoditel-v-yuzhno-sahalinske-poyavilas-karta-obschestvennyh-tualetov-194550')

page = bs(site.text, 'lxml')
points = page.select('div.Common_common__MfItd h3')
# print(points)

titles = [re.sub(r'\d+. ', '', str(point.text)) for point in points]
# print(titles)


points = page.select('div.Common_common__MfItd p')
comments = []
for point in points:
    if 'примечание' in point.text.lower():
        comments.append(point.text.replace('\xa0', ''))
        
# print(comments)


client = Client('ccbc665b-e2a1-4c7d-aff9-cd458b1317bb')

for i in range(1):
    print(titles[i])
    print(comments[i])
    
    # try:
    # print(titles[i])
    coord_Y, coord_X = client.coordinates(f"Южно-Сахалинск {titles[i]}")
    # coord_Y, coord_X = YaMapsTools.get_geo_coordinates(YaMapsTools.get_geo_toponym(f"Южно-Сахалинск {titles[i]}"))
        # coord_Y, coord_X = client.coordinates(f"Южно-Сахалинск Комсомолец")
    # except NothingFound:
    #     pass
    
    print(coord_X, coord_Y)
    print()
    
    
# 46.964696, 142.728851

# 46.938381, 142.759178
# YaMapsTools.get_map_response("Южно-сахалинск Горького 25")