import numpy as np
import cv2

p1_data = np.array([10, 25, 1])
p2_data = np.array([40, 63, 2])
players = np.array([p1_data, p2_data])
blank_color = np.array([255, 255, 255])

def get_player(image,x,y):
    if x != -1 or y != -1:
        hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
        pixel = hsv[x,y]
        if pixel[1] < 50 and pixel[2] > 150:
            return 0
        for player_data in players:
            if pixel[0] > player_data[0] and pixel[0] < player_data[1]:
                return player_data[2]
        return -1
    return -1

def create_matrix(image, rows):
    matrix = np.empty((7,3))
    i = 0
    for row in rows:
        for x in range(3):
            matrix[i][x] = get_player(image, row[x][1],row[x][0]) 
        i+=1
    return matrix

def process(image,circles):
    if len(circles) == 19:
        circles_sorted = np.sort(circles.view('i8,i8,i8'),axis=0,order=['f1']).view(np.int).reshape((-1,3))
        rows = np.asarray([]).reshape((-1, 3, 3)).astype(np.int)
        for i in [0,3,6,9,10,13,16]:
            if i==9:
                empty = np.array([(-1,-1,-1)]).view('i8,i8,i8')
                center_row = np.append(empty, circles_sorted[i:i+1].view('i8,i8,i8'), axis=0)
                center_row = np.append(center_row, empty, axis=0)
                rows = np.append(rows, center_row.view(np.int).reshape((1,3,3)),axis=0)
                continue
            rows = np.append(rows, np.sort(circles_sorted[i:i+3].view('i8,i8,i8'),axis=0,order=['f0']).view(np.int).reshape((1,3,3)), axis=0)
        cv2.imshow(f'circle1',image[319-25:319+25,247-25:247+25])
        print(create_matrix(image, rows))
    else:
        print(f'niepełne: {len(circles)}')