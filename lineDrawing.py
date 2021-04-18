import os
import skimage
import matplotlib.pyplot as plt
import numpy as np
from skimage import data, color, io

# Class which handles the conversion from normal to line drawing
# Takes an ndarray image as parameter
class LineDraw():

    def __init__(self, image):
        self.origImage = image
        self.imgWidth = len(image[0])
        self.imgHeight = len(image)
        
        #Sobel gradient map
        self.gX = None
        self.gY = None

    # Compute Sobel gradient map in the x direction for given image
    # Handle edge pixels by ignoring them
    # ndarray image
    def computeSobel(self, image, isXDirection):

        width = len(image[0])
        height = len(image)

        sobel = np.array([[[0.0 for rgb in range(3)] for x in range(width)] for y in range(height)], dtype='uint16')
        
        for y in range(1, height - 1):
            for x in range(1, width - 1):
                for rgb in range(3):
                    # kernel convolution                    
                    if (isXDirection):  # compute x gradient map
                        sobel[y][x][rgb] = (np.uint16(image[y - 1][x - 1][rgb]) + # top left                               
                                                      image[y + 1][x - 1][rgb]  + # bottom left
                                                      image[y][x - 1][rgb] * 2  + # left
                                                     -image[y - 1][x + 1][rgb]  + # top right
                                                     -image[y + 1][x + 1][rgb]  + # top right
                                                     -image[y][x + 1][rgb] * 2)   # right 

                    else:               # compute y gradient map
                        sobel[y][x][rgb] = (np.uint16(image[y - 1][x - 1][rgb]) + # top left                                 
                                                      image[y - 1][x + 1][rgb]  + # top right
                                                      image[y - 1][x][rgb] * 2  + # top
                                                     -image[y + 1][x - 1][rgb]  + # bottom left
                                                     -image[y + 1][x + 1][rgb]  + # bottom right
                                                     -image[y + 1][x][rgb] * 2)   # bottom 
        
        return color.rgb2gray(sobel)

    # Return converted image
    def convertToLine(self):
        sobelX = self.computeSobel(self.origImage, True)
        sobelY = self.computeSobel(self.origImage, False)

        gradientMap = 

        return 


def main():
    img = io.imread('test.jpg')
    ld = LineDraw(img)

    print(ld.imgWidth)
    print(ld.imgHeight)

    plt.figure()
    plt.imshow(img)
    plt.figure()
    plt.imshow(ld.convertToLine())
    io.show()

if __name__ == "__main__":
    main()