import os
import skimage
import matplotlib.pyplot as plt
import numpy as np
import math
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
    def computeSobelMaps(self, image):

        width = len(image[0])
        height = len(image)

        sobelX = np.array([[0 for x in range(width)] for y in range(height)])
        sobelY = np.array([[0 for x in range(width)] for y in range(height)])
        sobelMagnitude = np.array([[0 for x in range(width)] for y in range(height)])
        
        maxMagnitude = 0

        for y in range(1, height - 1):
            for x in range(1, width - 1):
                # kernel convolution                    
                sobelX[y][x] = (np.uint16(image[y - 1][x - 1]) + # top left                               
                                          image[y + 1][x - 1]  + # bottom left
                                          image[y][x - 1] * 2  + # left
                                         -image[y - 1][x + 1]  + # top right
                                         -image[y + 1][x + 1]  + # top right
                                         -image[y][x + 1] * 2)   # right 
                                                
                sobelY[y][x] = (np.uint16(image[y - 1][x - 1]) + # top left                                 
                                          image[y - 1][x + 1]  + # top right
                                          image[y - 1][x] * 2  + # top
                                         -image[y + 1][x - 1]  + # bottom left
                                         -image[y + 1][x + 1]  + # bottom right
                                         -image[y + 1][x] * 2)   # bottom 

                sobelMagnitude[y][x] = math.sqrt((sobelX[y][x] ** 2) + (sobelY[y][x] ** 2))

                # keep track of max value to use in normalization
                if (maxMagnitude < sobelMagnitude[y][x]):
                    maxMagnitude = sobelMagnitude[y][x]

        # normalize magnitudes
        for y in range(1, height - 1):
            for x in range(1, width - 1):
                sobelMagnitude[y][x] = (sobelMagnitude[y][x] / maxMagnitude) * 255

        return (sobelX, sobelY, sobelMagnitude)

    # Return converted image
    def convertToLine(self):
        sobelMaps = self.computeSobelMaps(color.rgb2gray(self.origImage))   

        return sobelMaps


def main():
    img = io.imread('test.jpg')
    ld = LineDraw(img)

    print(ld.imgWidth)
    print(ld.imgHeight)

    maps = ld.convertToLine()
    plt.figure("Original")
    plt.imshow(img)

    for map in maps:
        plt.figure()
        plt.imshow(map, cmap='gray')        

    io.show()

if __name__ == "__main__":
    main()