using System;
using System.Drawing;
using System.IO;
using System.Numerics; // Needed for BigInteger

#nullable enable

class Program
{
    static string? ValidateInput(string? input, out int canvasDimension)
    {
        // We check if it is a valid integer, that fits in the int range
        if (int.TryParse(input, out canvasDimension))
        {
            // We check if it is positive
            if (canvasDimension >= 0)
                return null; // Return null if valid
            else
                return "Please enter a number >= 0.";
        }

        // If it's parsed, then it's a number
        return BigInteger.TryParse(input, out _)
            ? "Please enter a number less than 2,147,483,648."
            : "Please enter a valid number.";
    }


    static bool ValidateRectangle(string line, out Rectangle? rectangle)
    {
        // We assign null to our rectangle since it needs to have a value 
        // when we return from the function due to it being an out tracking reference,
        // and if there are not enough fields to form one, we return false and leave it
        // as null
        rectangle = null;
        string[] parts = line.Split(' '); // The separator is a single space

        if (parts.Length != 5)
            return false; // Not enough values to form a valid rectangle

        string rectangleName = parts[0];
        // The coordinates, widths and heights are guaranteed valid integers
        int rectangleBottomLeftX = int.Parse(parts[1]);
        int rectangleBottomLeftY = int.Parse(parts[2]);
        int rectangleWidth = int.Parse(parts[3]);
        int rectangleHeight = int.Parse(parts[4]);

        // The widths and height are valid integers, but they could be negative
        if (rectangleWidth <= 0 || rectangleHeight <= 0)
            return false;

        // Everything is alright, so we will make our rectangle
        rectangle = new Rectangle(rectangleName, rectangleBottomLeftX, rectangleBottomLeftY, rectangleWidth, rectangleHeight);
        return true;
    }


    static bool IsRectangleInCanvas(Rectangle rectangle, int canvasWidth, int canvasHeight)
    {
        // We check if it's left corner is equal to or greater than our (0,0) origin
        // and also if its dimensions fit in the canvas
        return rectangle.X >= 0 && rectangle.Y >= 0 &&
            ((rectangle.X + rectangle.Width) <= canvasWidth) && 
            ((rectangle.Y + rectangle.Height) <= canvasHeight);
    }


    static string RectangleToString(Rectangle rectangle)
    {
        return $"Name: {rectangle.Name}, X: {rectangle.X}, " +
                $"Y: {rectangle.Y}, Width: {rectangle.Width}, Height: {rectangle.Height}";
    }


    static List<Rectangle> GetNonOverlappingRectangles(List<Rectangle> rectangleList)
    {
        // We keep a HashSet of rectangles which overlap to allow constant-time lookup
        HashSet<Rectangle> overlappingRectangles = [];

        for (int i = 0; i < rectangleList.Count; i++)
            for (int j = i + 1; j < rectangleList.Count; j++)
                // If there's overlap between these two, we add them to our set
                if (Util.FindOverlap(rectangleList[i], rectangleList[j]) != null)
                {
                    overlappingRectangles.Add(rectangleList[i]);
                    overlappingRectangles.Add(rectangleList[j]);
                }

        // We return all rectangles not in the overlapping rectangles HashSet
        return [.. rectangleList.Where(rect => !overlappingRectangles.Contains(rect))];
    }


    static bool IsRectangleCompletelyIncluded(Rectangle sourceRectangle, Rectangle targetRectangle)
    {
        // Overlap rectangle can be null if no overlap is present so we make it nullable
        Rectangle? overlapRectangle = Util.FindOverlap(sourceRectangle, targetRectangle);

        // We check if it is null, and then if it's width and height are equal to our
        // target rectangle. This is enough to tell if it's completely included, since
        // if the overlap is as big as the rectangle itself then it's completely included
        return overlapRectangle != null && overlapRectangle.Width == targetRectangle.Width 
                                        && overlapRectangle.Height == targetRectangle.Height;
    }

    static List<Rectangle> GetCompletelyIncludedRectangles(List<Rectangle> rectangleList)
    {
        // We return our rectangle list where, for a target rectangle, any rectangle in our
        // list different from the target is completely included in it
        return [.. rectangleList.Where(target =>
            rectangleList.Any(source => source != target && IsRectangleCompletelyIncluded(source, target))
        )];
    }


    static long CalculateUncoveredArea(int canvasWidth, int canvasHeight, List<Rectangle> rectangles)
    {
        // We will use a sweep line approach.
        // Total area of the canvas
        long totalCanvasArea = (long)canvasWidth * canvasHeight;

        // List of events: each rectangle contributes two events (start & end)
        List<(int x, int y1, int y2, int type)> events = [];

        foreach (var rect in rectangles)
        {
            events.Add((rect.X, rect.Y, rect.Y + rect.Height, 1));   // Opening event: 1
            events.Add((rect.X + rect.Width, rect.Y, rect.Y + rect.Height, -1)); // Closing event: -1
        }

        // Sort events: first by x, then by type (-1 first to close before opening at same x)
        events.Sort((a, b) => a.x != b.x ? a.x.CompareTo(b.x) : a.type.CompareTo(b.type));

        long coveredArea = 0;
        int prevX = 0;
        SortedList<int, int> activeIntervals = [];

        foreach (var (x, y1, y2, type) in events)
        {
            // Calculate covered width from previous x position
            if (x > prevX)
            {
                int coveredHeight = 0, lastY = -1;
                // We take the active intervals
                foreach (var (start, end) in activeIntervals)
                {
                    // If the start is bigger than the last y
                    if (start > lastY) coveredHeight += end - start;
                    else if (end > lastY) coveredHeight += end - lastY;
                    lastY = Math.Max(lastY, end);
                }

                coveredArea += (long)(x - prevX) * coveredHeight;
            }

            // Update active intervals
            if (type == 1) // Opening event
            {
                if (!activeIntervals.TryGetValue(y1, out int value)) activeIntervals[y1] = y2;
                else activeIntervals[y1] = Math.Max(value, y2);
            }
            else // Closing event
            {
                activeIntervals.Remove(y1);
            }

            prevX = x;
        }

        return totalCanvasArea - coveredArea;
    }


    static void Main()
    {
        string? inputted_string;
        int canvasWidth, canvasHeight;

        // Canvas width input loop
        while (true)
        {
            Console.Write("Canvas width: ");
            inputted_string = Console.ReadLine();

            string? validationResult = ValidateInput(inputted_string, out canvasWidth);
            if (validationResult == null)
                break; // Valid input, break the loop
            else
                Console.WriteLine(validationResult); // Print error message
        }

        // Canvas height input loop (same logic as width)
        while (true)
        {
            Console.Write("Canvas height: ");
            inputted_string = Console.ReadLine();

            string? validationResult = ValidateInput(inputted_string, out canvasHeight);
            if (validationResult == null)
                break; // Valid input, break the loop
            else
                Console.WriteLine(validationResult); // Print error message
        }

        // We first check if the input file even exists
        if (!File.Exists("input.txt"))
        {
            Console.WriteLine("Error: The file 'input.txt' was not found.");
            return;
        }


        // We read from the input file line by line, so we will use a StreamReader
        // We utilize the using keyword, which will automatically call the Dispose() method
        // on the reader object once it goes out of scope, ensuring efficient memory management
        using StreamReader reader = new("input.txt");

        List<Rectangle> rectangleList = [];
        string? line;

        // Each rectangle is declared on a separate line
        while ((line = reader.ReadLine()) != null)
        {
            if (!ValidateRectangle(line, out Rectangle? newRectangle))
                // If it is not valid, we will just continue with
                // the next line in the file. No errors need to be
                // outputted
                continue;

            // If the rectangle is valid and it is in the canvas, we will
            // add it to our list of rectangles, since we will only need the
            // rectangles which are in the canvas going further
            if (IsRectangleInCanvas(newRectangle!, canvasWidth, canvasHeight))
                rectangleList.Add(newRectangle!);
        }

        // List the rectangles included in the canvas area
        // We just print our list since it contains only rectangles in the canvas
        Console.WriteLine();
        Console.WriteLine("Rectangles in canvas:");
        foreach (var rectangle in rectangleList)
            Console.WriteLine(RectangleToString(rectangle));

        // List the rectangles that don't overlap with other rectangles
        // We make a copy of the rectangleList to not modify our original one, since
        // in C# reference types are passed by reference, so modifications in the
        // function can be also observed outside of it
        // See the GetNonOverlappingRectangles function
        Console.WriteLine();
        Console.WriteLine("Rectangles with no overlap:");
        foreach (var rectangle in GetNonOverlappingRectangles([.. rectangleList]))
            Console.WriteLine(RectangleToString(rectangle));

        // List the rectangles which are included completely in another
        // rectangle
        // We again make a copy of it. See the GetCompletelyIncludedRectangles function
        Console.WriteLine();
        Console.WriteLine("Rectangles in another rectangle:");
        foreach (var rectangle in GetCompletelyIncludedRectangles([.. rectangleList]))
            Console.WriteLine(RectangleToString(rectangle));

        // List the uncovered area of the canvas
        // See the CalculateUncoveredArea function
        Console.WriteLine();
        Console.WriteLine("Free area in canvas: " + CalculateUncoveredArea(canvasWidth, canvasHeight, rectangleList));
    }
}