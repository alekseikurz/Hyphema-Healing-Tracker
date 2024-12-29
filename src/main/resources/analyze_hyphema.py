import cv2
import numpy as np
import sys
import json
import os

# Distance threshold for Region Growing
distance = 15
coefficient_for_threshold = 0.9 # Find the start point for Region Growing

# Class for pixel object
class Pixel:
    def __init__(self, y, x):
        self.y = y
        self.x = x

    def getCoordinateY(self):
        return self.y

    def getCoordinateX(self):
        return self.x
    

# Funktion für Closing-Operation
def apply_closing(img, kernel_size=5):
    kernel = np.ones((kernel_size, kernel_size), np.uint8)  # Kernel definieren
    # Erstelle eine Maske für grüne Pixel
    green_mask = (img[:, :, 1] == 255) & (img[:, :, 0] == 0) & (img[:, :, 2] == 0)
    green_mask = green_mask.astype(np.uint8) * 255

    # Morphologische Closing-Operation anwenden
    green_mask_closed = cv2.morphologyEx(green_mask, cv2.MORPH_CLOSE, kernel)

    # Aktualisiere das grün markierte Bild basierend auf der geschlossenen Maske
    img[green_mask_closed == 255] = [0, 255, 0]

    return img

def draw_percentage_on_image(image, percentage):
    """
    Zeichnet den Prozentsatz in die linke obere Ecke des Bildes.

    Args:
        image (numpy.ndarray): Das Bild, auf dem der Prozentsatz angezeigt werden soll.
        percentage (int): Der Prozentsatz, der angezeigt werden soll.

    Returns:
        numpy.ndarray: Das Bild mit dem gezeichneten Prozentsatz.
    """
    percentage_text = f"{percentage}%"
    font = cv2.FONT_HERSHEY_SIMPLEX
    font_scale = 1
    font_thickness = 2
    #color = (0, 255, 0)  # Dunkel orange in BGR
    color = (255, 51, 51)  # Dunkel orange in BGR
    position = (10, 30)  # Linke obere Ecke

    cv2.putText(image, percentage_text, position, font, font_scale, color, font_thickness, cv2.LINE_AA)
    return image


# # Find the start point for Region Growing
# def findStartPointForRegionGrowing(image, best_circle):
#     image_GRAY = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
#     center_x, center_y, radius = best_circle

#     list_original = image_GRAY[center_y + 5 : center_y + radius - 1, center_x]
#     list_central_differential_coefficient = [-1]

#     for i in range(1, len(list_original) - 1):
#         cdc_value = abs(-1 * list_original[i - 1] + list_original[i + 1])
#         list_central_differential_coefficient.append(cdc_value)
#     list_central_differential_coefficient.append(list_central_differential_coefficient[-1])

#     threshold = np.max(list_central_differential_coefficient) * 0.3
#     start_point_index = -1

#     for i in range(1, len(list_central_differential_coefficient)):
#         if (list_central_differential_coefficient[i] > threshold and
#             list_central_differential_coefficient[i] > list_central_differential_coefficient[i - 1]):
#             start_point_index = i + center_y + 5
#             break

#     if start_point_index == -1:
#         return None

#     return center_x, start_point_index

# Find the start point for Region Growing
def findStartPointForRegionGrowing(image, best_circle):
    image_GRAY = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    center_x, center_y, radius = best_circle

    # Extrahiere den relevanten Grauwert-Bereich
    list_original = image_GRAY[center_y + 5 : center_y + radius - 1, center_x]
    list_central_differential_coefficient = [-1]

    # Berechnung der zentralen Differenzen
    for i in range(1, len(list_original) - 1):
        cdc_value = abs(-1 * list_original[i - 1] + list_original[i + 1])
        list_central_differential_coefficient.append(cdc_value)
    list_central_differential_coefficient.append(list_central_differential_coefficient[-1])

    # print("--- list_central_differential_coefficient :")
    # for i in range(1, len(list_central_differential_coefficient)):
    #     print(i, " : " , list_central_differential_coefficient[i], " ")
    # print("--- list_central_differential_coefficient")

    # Schwellwert für die Differenz
    threshold = np.max(list_central_differential_coefficient) * coefficient_for_threshold

    start_point_index = -1
    end_point_index = -1

    # Finde den Anfang des interessierenden Bereichs
    for i in range(1, len(list_central_differential_coefficient)):
        if (list_central_differential_coefficient[i] > threshold and
            list_central_differential_coefficient[i] > list_central_differential_coefficient[i - 1]):
            start_point_index = i + center_y + 5
            break

    if start_point_index == -1:
        return None, None, None

    end_point_index = center_y + radius - 1

    if end_point_index == -1:
        end_point_index = radius + center_y - 1

    # Berechne die finale Startpunkt-Koordinate als Mittelpunkt zwischen Start- und Endpunkt
    # final_point_index = (start_point_index + end_point_index) // 2 # original
    final_point_index = ((start_point_index + end_point_index) // 2)


    # print("indexes : ")
    # print("start_point_index : " , start_point_index - (center_y + 5))
    # print("end_point_index : " , end_point_index - (center_y + 5)) 
    # print("final_point_index : " , final_point_index - (center_y + 5))

    return center_x, final_point_index

# Find the optimal circle
def find_optimal_circle(image, gray, min_radius, step=0.02):
    height, width, _ = image.shape
    best_circle = None
    smallest_delta = float("inf")

    start_max_radius = round(height / 2.0)
    end_max_radius = round(height / 2.2)
    max_radius_values = np.arange(end_max_radius, start_max_radius + 1, step)

    for varMaxRadius in max_radius_values:
        circles = cv2.HoughCircles(
            gray,
            cv2.HOUGH_GRADIENT,
            dp=1,
            minDist=round(height / 2.12),
            param1=90,
            param2=22,
            minRadius=min_radius,
            maxRadius=int(varMaxRadius),
        )

        if circles is not None:
            circles = np.uint16(np.around(circles))
            for circle in circles[0, :]:
                center = (circle[0], circle[1])
                radius = circle[2]
                delta_sum = 0  # Simplified for illustration purposes

                if delta_sum < smallest_delta:
                    smallest_delta = delta_sum
                    best_circle = circle

    return best_circle

# # Implements Region Growing
# def regionGrowing(image, start_x, start_y):
#     rows, cols = image.shape[:2]
#     imgBuffer = np.zeros((rows, cols), dtype=np.uint8)
#     grayValueStartPixel = image[start_y, start_x, 0]
#     stack = [Pixel(start_y, start_x)]

#     while stack:
#         currentPixel = stack.pop()
#         imgBuffer[currentPixel.getCoordinateY(), currentPixel.getCoordinateX()] = 255

#         for dy, dx in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
#             ny, nx = currentPixel.getCoordinateY() + dy, currentPixel.getCoordinateX() + dx
#             if (0 <= ny < rows and 0 <= nx < cols and
#                 imgBuffer[ny, nx] == 0 and
#                 (grayValueStartPixel - distance) <= image[ny, nx, 0] <= (grayValueStartPixel + distance)):
#                 stack.append(Pixel(ny, nx))

#     area = np.sum(imgBuffer == 255)  # Berechne die Fläche

#     image[imgBuffer == 255] = [0, 255, 0] # Set green color where imgBuffer is 255

#     return image, area

# Implements Region Growing
def regionGrowing(image, start_x, start_y):

    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    image = cv2.cvtColor(gray, cv2.COLOR_GRAY2BGR)


    rows, cols = image.shape[:2]
    imgBuffer = np.zeros((rows, cols), dtype=np.uint8)

    grayValueStartPixel = image[start_y, start_x, 0]
    stack = [Pixel(start_y, start_x)]

    while stack:
        currentPixel = stack.pop()
        imgBuffer[currentPixel.getCoordinateY(), currentPixel.getCoordinateX()] = 255

        for dy, dx in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
            ny, nx = currentPixel.getCoordinateY() + dy, currentPixel.getCoordinateX() + dx
            if (0 <= ny < rows and 0 <= nx < cols and
                imgBuffer[ny, nx] == 0 and
                (grayValueStartPixel - distance) <= image[ny, nx, 0] <= (grayValueStartPixel + distance)):
                stack.append(Pixel(ny, nx))

    area = np.sum(imgBuffer == 255)  # Berechne die Fläche

    image[imgBuffer == 255] = [0, 255, 0] # Set green color where imgBuffer is 255

    return image, area

# # Function for Region Growing
# def regionGrowing(img_input, x, y):
#     # Konvertiere das Bild zu Graustufen und dann zurück zu BGR
#     gray = cv2.cvtColor(img_input, cv2.COLOR_BGR2GRAY)
#     img_input = cv2.cvtColor(gray, cv2.COLOR_GRAY2BGR)

#     # Grauwert des Startpixels (aus der Blau-Komponente des BGR-Bildes)
#     grayValueStartPixel = img_input[y, x, 0]

#     # Bilddimensionen bestimmen
#     rows, cols = img_input.shape[:2]
#     imgBuffer = np.zeros((rows, cols), dtype=np.uint8)  # Puffer für die Region
#     imgBuffer[y, x] = 255

#     # Stack für Nachbarpixel initialisieren
#     stack = [Pixel(y, x)]

#     # Region Growing
#     while stack:
#         current_pixel = stack.pop()
#         cy = current_pixel.getCoordinateY()
#         cx = current_pixel.getCoordinateX()

#         # Untersuche die 4 Nachbarpixel
#         for ny, nx in [(cy - 1, cx), (cy + 1, cx), (cy, cx - 1), (cy, cx + 1)]:
#             if 0 <= ny < rows and 0 <= nx < cols:  # Innerhalb des Bildes
#                 if imgBuffer[ny, nx] == 0:  # Noch nicht besucht
#                     neighborValue = img_input[ny, nx, 0]
#                     if grayValueStartPixel - distance <= neighborValue <= grayValueStartPixel + distance:
#                         imgBuffer[ny, nx] = 255
#                         stack.append(Pixel(ny, nx))

#     # Färbe die Region im Originalbild grün
#     img_input[imgBuffer == 255] = [0, 255, 0]  # Grün markieren

#     # Morphologische Closing-Operation auf die grün markierten Pixel anwenden
#     img_input = apply_closing(img_input, 5)
#     img_input = apply_closing(img_input, 15)
#     img_input = apply_closing(img_input, 5)
#     img_input = apply_closing(img_input, 5)

#     # Berechne die Fläche der Region
#     area = np.sum(imgBuffer == 255)  # Berechne die Fläche

#     # Rückgabe von img_input und der Fläche, wie in Funktion 1
#     return img_input, area


# Main
if __name__ == "__main__":
    if len(sys.argv) != 2:
        print(json.dumps({"error": "Invalid arguments. Usage: analyze_hyphema.py <image_path>"}))
        sys.exit(1)

    image_path = sys.argv[1]
    try:
        # Lade das Bild
        image = cv2.imread(image_path)
        if image is None:
            raise FileNotFoundError(f"Image not found: {image_path}")
        
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        gray = cv2.medianBlur(gray, 3)

        best_circle = find_optimal_circle(image, gray, min_radius=8)
        if best_circle is not None:
            center_x, center_y, radius = best_circle
            start_x, start_y = findStartPointForRegionGrowing(image, best_circle)

            if start_x is not None and start_y is not None:
                image, region_area = regionGrowing(image, start_x, start_y)
                total_area = np.pi * radius ** 2
                percentage = (region_area / total_area) * 100

                percentage = int(percentage)

                # Zeichne den Prozentsatz auf das Bild
                image = draw_percentage_on_image(image, percentage)

                # Überschreibe das Originalbild mit der bearbeiteten Version
                cv2.imwrite(image_path, image)

                # JSON-Ergebnis
                result = {
                    "image": image_path,  # Der Pfad bleibt derselbe
                    "hyphema_area_percentage": percentage
                }
                print(json.dumps(result))  # JSON über stdout an Java senden
            else:
                print(json.dumps({"error": "Start point for region growing not found."}))
        else:
            print(json.dumps({"error": "No circle found."}))

    except Exception as e:
        print(json.dumps({"error": str(e)}))

    

# # Main
# if __name__ == "__main__":
#     if len(sys.argv) != 2:
#         print(json.dumps({"error": "Invalid arguments. Usage: analyze_hyphema.py <image_path>"}))
#         sys.exit(1)

#     image_path = sys.argv[1]
#     try:
#         # Lade das Bild
#         image = cv2.imread(image_path)
#         if image is None:
#             raise FileNotFoundError(f"Image not found: {image_path}")
        
#         gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
#         gray = cv2.medianBlur(gray, 3)

#         best_circle = find_optimal_circle(image, gray, min_radius=8)
#         if best_circle is not None:
#             center_x, center_y, radius = best_circle
#             start_x, start_y = findStartPointForRegionGrowing(image, best_circle)

#             if start_x is not None and start_y is not None:
#                 image, region_area = regionGrowing(image, start_x, start_y)
#                 total_area = np.pi * radius ** 2
#                 percentage = (region_area / total_area) * 100

#                 percentage = int(percentage)

#                 # Save the processed image
#                 base_name, ext = os.path.splitext(os.path.basename(image_path))
#                 new_image_name = f"{base_name}_new{ext}"
#                 new_image_path = os.path.join(os.path.dirname(image_path), new_image_name)
#                 cv2.imwrite(new_image_path, image)

#                 # JSON-Ergebnis
#                 result = {
#                     "image": new_image_path,  # Neuer Pfad des bearbeiteten Bildes
#                     "hyphema_area_percentage": percentage
#                 }
#                 print(json.dumps(result))  # JSON über stdout an Java senden
#             else:
#                 print(json.dumps({"error": "Start point for region growing not found."}))
#         else:
#             print(json.dumps({"error": "No circle found."}))

#     except Exception as e:
#         print(json.dumps({"error": str(e)}))

# # Main
# if __name__ == "__main__":
#     if len(sys.argv) != 2:
#         print(json.dumps({"error": "Invalid arguments. Usage: analyze_hyphema.py <image_path>"}))
#         sys.exit(1)

#     image_path = sys.argv[1]
#     try:
#         # Lade das Bild
#         image = cv2.imread(image_path)
#         if image is None:
#             raise FileNotFoundError(f"Image not found: {image_path}")
        
#         gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
#         gray = cv2.medianBlur(gray, 3)

#         best_circle = find_optimal_circle(image, gray, min_radius=8)
#         if best_circle is not None:
#             center_x, center_y, radius = best_circle
#             start_x, start_y = findStartPointForRegionGrowing(image, best_circle)

#             if start_x is not None and start_y is not None:
#                 image, region_area = regionGrowing(image, start_x, start_y)
#                 total_area = np.pi * radius ** 2
#                 percentage = (region_area / total_area) * 100

#                 # save image
#                 cv2.imwrite('S_11_new.jpg', image)

#                 # JSON-Ergebnis
#                 result = {
#                     "image": image_path,
#                     "hyphema_area_percentage": percentage
#                 }
#                 print(json.dumps(result))
#             else:
#                 print(json.dumps({"error": "Start point for region growing not found."}))
#         else:
#             print(json.dumps({"error": "No circle found."}))

#     except Exception as e:
#         print(json.dumps({"error": str(e)}))


