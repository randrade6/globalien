# globalien

To execute this application, run the JAR file in `out/artifacts/globalien_jar/globalien.jar`.

##Available command line arguments:

  **-s or --search** (required): Text to search (Ex: -s Java).
  **-r or --results** (optional): Number of results to show (how many URLs to show). Defaults to 10.
  **-i or --initial** (optional): Initial URL to crawl. Should be a valid Wikipedia URL. Defaults to https://en.wikipedia.org/wiki/Java_(programming_language).
  **-d or --depth** (optional): Number of URLs to crawl from the initial URL. Defaults to 0.
  **-c or --clear** (optional, no arguments needed): Clears the index.
  **-h or --help** (optional): Displays help.