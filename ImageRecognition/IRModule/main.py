import cv2
import numpy as np
import time
import sys
from detection import process

def get_cam_image(cap,id:int):
    if not cap.isOpened:
        cap.open(id)
    
    out, _ = cap.read()
    while not out:
        time.sleep(0.3)
    _, frame = cap.read()
    print(cap.read())
    if frame is not None:
        frame = frame[0:frame.shape[0], 160:frame.shape[1]-160]
        frame = cv2.rotate(frame,cv2.ROTATE_90_CLOCKWISE)
        frame = cv2.resize(frame, (int(frame.shape[1]/1.5), int(frame.shape[0]/1.5)))
    return frame
    
def get_image(imagename:str):
    image = cv2.imread(imagename)
    return image

print(f'Opencv2 v{cv2.getVersionMajor()}.{cv2.getVersionMinor()}.{cv2.getVersionRevision()}')

cap = cv2.VideoCapture()

circles_type = ('x',int), ('y',int), ('r',int)
try:
    step = -1
    while(True):
        frame = get_cam_image(cap,0)
        # frame = get_image('board.jpg')

        if time.time() - step >= 1:
            output = frame.copy()
            frame_gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            cos, frame_gray = cv2.threshold(frame_gray,100,255,cv2.THRESH_BINARY)
            #frame_gray = cv2.blur(frame_gray, (5, 5))
            # detect circles in the image
            circles = cv2.HoughCircles(frame_gray, cv2.HOUGH_GRADIENT, 1.5, frame_gray.shape[1]/9, param1=90, param2=20,minRadius=20, maxRadius=30)
            # ensure at least some circles were found
            if circles is not None:
                # convert the (x, y) coordinates and radius of the circles to integers
                circles = np.round(circles[0, :]).astype("int")

                # loop over the (x, y) coordinates and radius of the circles
                for (x, y, r) in circles:
                    # draw the circle in the output image, then draw a rectangle
                    # corresponding to the center of the circle
                    cv2.circle(output, (x, y), r, (0, 255, 0), 4)
                    cv2.rectangle(output, (x - 5, y - 5), (x + 5, y + 5), (0, 128, 255), -1)
                
                # show the output image
                
                process(frame, circles)
                step = time.time()
        cv2.imshow("output", np.hstack([frame, output]))
        cv2.imshow("gray", frame_gray)
        cv2.waitKey(100)
except KeyboardInterrupt:
    cap.release()
    print('elo')


