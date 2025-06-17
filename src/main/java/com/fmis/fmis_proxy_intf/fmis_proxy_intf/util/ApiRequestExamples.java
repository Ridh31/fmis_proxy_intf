package com.fmis.fmis_proxy_intf.fmis_proxy_intf.util;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.HeaderConstants;

/**
 * This class contains examples of API requests for different use cases.
 * These examples demonstrate how to send requests to the FMIS Proxy Interface API in various programming languages and tools.
 *
 * {@code @cURL} Example
 * {@code @JavaScript} Example
 * {@code @Python} Example
 * {@code @Java} Example
 * {@code @C#} Example
 * {@code @PHP} Example
 * {@code @Node.js} Example
 * @code @Ruby} Example
 * {@code @Go} Example
 */
public class ApiRequestExamples {

    // Base URL and other constants
    private static final String CAMDX_BANK_STATEMENT_SERVICE_CODE = "/import-bank";
    private static final String CAMDX_BANK_STATEMENT_URL = "{{CAMDX_URL}}/r1/CAMBODIA/GOV/CAMDX-2024031801/FMIS_INTF_API/" + CAMDX_BANK_STATEMENT_SERVICE_CODE;
    private static final String PREFIX = "/api/v1";
    private static final String BASE_URL = "{{FMIS_URL}}" + PREFIX;
    private static final String CONTENT_TYPE = "Content-Type: " + HeaderConstants.CONTENT_TYPE_JSON;
    private static final String X_ROAD_CLIENT = "X-Road-Client: camdx_client_id";
    private static final String X_PARTNER_TOKEN = "X-Partner-Token: partner_token";
    public static final String CREATE_PARTNER_URL = BASE_URL + "/create-partner";
    private static final String GET_ALL_PARTNERS_URL = BASE_URL + "/list-partner";
    private static final String IMPORT_BANK_STATEMENT_URL = BASE_URL + "/import-bank-statement";

    // Language constants
    public static final String CURL = "cURL";
    public static final String JAVASCRIPT = "JavaScript (Fetch API)";
    public static final String PYTHON = "Python (requests)";
    public static final String JAVA_OKHTTP = "Java (OkHttp)";
    public static final String JAVA_HTTPURLCONNECTION = "Java (HttpURLConnection)";
    public static final String CSHARP = "C# (HttpClient)";
    public static final String PHP_CURL = "PHP (cURL)";
    public static final String NODEJS = "Node.js (Axios)";
    public static final String RUBY = "Ruby (Net::HTTP)";
    public static final String GO = "Go (net/http)";

    /**
     * Partner JSON Example
     */
    public static final String PARTNER_JSON =
        "{" +
        "\"name\": \"FMIS\"," +
        "\"description\": \"Financial Management Information System\"," +
        "\"code\": \"FMIS\"" +
        "}";
    
    /**
     * cURL Example for creating a new partner.
     * This shows how to use cURL to send a POST request with the partner data in JSON format.
     */
    public static final String CREATE_PARTNER_CURL =
        "curl -X POST \"" + CREATE_PARTNER_URL + "\" \\" +
        "\n-H \"" + CONTENT_TYPE + "\" \\" +
        "\n-H \"" + X_PARTNER_TOKEN + "\" \\" +
        "\n-d '" + PARTNER_JSON + "'";

    /**
     * JavaScript (Fetch) Example for creating a new partner.
     * This demonstrates how to use the Fetch API in JavaScript to send a POST request with JSON data.
     */
    public static final String CREATE_PARTNER_JS_FETCH =
        "fetch('" + CREATE_PARTNER_URL + "', {\n" +
        "    method: 'POST',\n" +
        "    headers: { '" + CONTENT_TYPE + "', '" + X_PARTNER_TOKEN + "' },\n" +
        "    body: JSON.stringify(" + PARTNER_JSON + ")\n" +
        "})\n" +
        "    .then(response => response.json())\n" +
        "    .then(data => console.log(data));";

    /**
     * Python (requests) Example for creating a new partner
     * This example demonstrates how to send a POST request to the API using the Python `requests` library.
     * It includes the URL, headers, and body data, and prints the JSON response from the server.
     */
    public static final String CREATE_PARTNER_PYTHON =
        "import requests\n\n" +
        "url = '" + CREATE_PARTNER_URL + "'\n" +
        "headers = {'" + CONTENT_TYPE + "', '" + X_PARTNER_TOKEN+ "'}\n" +
        "data = " + PARTNER_JSON + "\n\n" +
        "response = requests.post(url, json=data, headers=headers)\n" +
        "print(response.json())";

    /**
     * Java (OkHttp) Example for creating a new partner
     * This example demonstrates how to send a POST request using the OkHttp library in Java.
     * It sets the request body, adds the necessary headers, and executes the request to the server.
     */
    public static final String CREATE_PARTNER_JAVA_OKHTTP =
        "OkHttpClient client = new OkHttpClient();\n" +
        "MediaType mediaType = MediaType.parse(\"" + HeaderConstants.CONTENT_TYPE_JSON + "\");\n" +
        "RequestBody body = RequestBody.create(mediaType, \"" + PARTNER_JSON + "\");\n\n" +
        "Request request = new Request.Builder()\n" +
        "    .url(\"" + CREATE_PARTNER_URL + "\")\n" +
        "    .post(body)\n" +
        "    .addHeader(\"" + CONTENT_TYPE + "\")\n" +
        "    .addHeader(\"" + X_PARTNER_TOKEN + "\")\n" +
        "    .build();\n\n" +
        "Response response = client.newCall(request).execute();\n" +
        "System.out.println(response.body().string());";

    /**
     * C# (HttpClient) Example for creating a new partner
     * This example shows how to use C#'s `HttpClient` to send a POST request with JSON data.
     * It asynchronously sends the request, waits for the response, and then prints the result.
     */
    public static final String CREATE_PARTNER_CSHARP =
        "using System;\n" +
        "using System.Net.Http;\n" +
        "using System.Text;\n" +
        "using System.Threading.Tasks;\n\n" +
        "class Program\n" +
        "{\n" +
        "    static async Task Main()\n" +
        "    {\n" +
        "        using HttpClient client = new HttpClient();\n" +
        "        string url = \"" + CREATE_PARTNER_URL + "\";\n\n" +
        "        var json = \"" + PARTNER_JSON + "\";\n" +
        "        var content = new StringContent(json, Encoding.UTF8, \"" + HeaderConstants.CONTENT_TYPE_JSON + "\");\n\n" +
        "        client.DefaultRequestHeaders.Add(\"X-Partner-Token\", \"your_partner_token_here\");\n" +
        "        HttpResponseMessage response = await client.PostAsync(url, content);\n" +
        "        string result = await response.Content.ReadAsStringAsync();\n" +
        "        Console.WriteLine(result);\n" +
        "    }\n" +
        "}";

    /**
     * PHP (cURL) Example for creating a new partner
     * This example shows how to send a POST request in PHP using cURL.
     * The data is encoded as JSON, the necessary headers are set, and the response is echoed.
     */
    public static final String CREATE_PARTNER_PHP_CURL =
        "<?php\n" +
        "$url = '" + CREATE_PARTNER_URL + "';\n" +
        "$data = json_encode(" + PARTNER_JSON + ");\n\n" +
        "$ch = curl_init($url);\n" +
        "curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);\n" +
        "curl_setopt($ch, CURLOPT_HTTPHEADER, ['" + CONTENT_TYPE + "', '" + X_PARTNER_TOKEN + "']);\n" +
        "curl_setopt($ch, CURLOPT_POST, true);\n" +
        "curl_setopt($ch, CURLOPT_POSTFIELDS, $data);\n\n" +
        "$response = curl_exec($ch);\n" +
        "curl_close($ch);\n\n" +
        "echo $response;\n" +
        "?>";

    /**
     * Node.js (Axios) Example for creating a new partner
     * This example shows how to send a POST request using the `Axios` library in Node.js.
     * It sets the request body with the partner's data, sends the request, and logs the response.
     */
    public static final String CREATE_PARTNER_NODEJS =
        "const axios = require('axios');\n" +
        "const url = '" + CREATE_PARTNER_URL + "';\n" +
        "const data = " + PARTNER_JSON + ";\n\n" +
        "axios.post(url, data, { headers: { '" + CONTENT_TYPE + "', '" + X_PARTNER_TOKEN + "' } })\n" +
        "    .then(response => console.log(response.data))\n" +
        "    .catch(error => console.error(error));";

    /**
     * Ruby (Net::HTTP) Example for creating a new partner
     * This example demonstrates how to send a POST request in Ruby using the `Net::HTTP` library.
     * It prepares the request, sets the necessary headers, sends the request, and prints the response body.
     */
    public static final String CREATE_PARTNER_RUBY =
        "require 'net/http'\n" +
        "require 'json'\n\n" +
        "url = URI('" + CREATE_PARTNER_URL + "')\n" +
        "http = Net::HTTP.new(url.host, url.port)\n" +
        "http.use_ssl = true\n\n" +
        "request = Net::HTTP::Post.new(url, { '" + CONTENT_TYPE + "', '" + X_PARTNER_TOKEN + "' })\n" +
        "request.body = " + PARTNER_JSON + ".to_json\n\n" +
        "response = http.request(request)\n" +
        "puts response.body";

    /**
     * Go (net/http) Example for creating a new partner
     * This example demonstrates how to send a POST request in Go using the `net/http` package.
     * The request body is encoded as JSON, and the response status is printed after the request is executed.
     */
    public static final String CREATE_PARTNER_GO =
        "package main\n\n" +
        "import (\n" +
        "    \"bytes\"\n" +
        "    \"encoding/json\"\n" +
        "    \"fmt\"\n" +
        "    \"net/http\"\n" +
        ")\n\n" +
        "func main() {\n" +
        "    url := \"" + CREATE_PARTNER_URL + "\"\n" +
        "    data := " + PARTNER_JSON + "\n" +
        "    jsonData, _ := json.Marshal(data)\n\n" +
        "    req, _ := http.NewRequest(\"POST\", url, bytes.NewBuffer(jsonData))\n" +
        "    req.Header.Set(\"" + CONTENT_TYPE + "\")\n" +
        "    req.Header.Set(\"X-Partner-Token\", \"your_partner_token_here\")\n\n" +
        "    client := &http.Client{}\n" +
        "    resp, _ := client.Do(req)\n" +
        "    defer resp.Body.Close()\n\n" +
        "    fmt.Println(resp.Status)\n" +
        "}";

    /**
     * cURL Example for getting all partners with pagination
     */
    public static final String GET_ALL_PARTNERS_CURL = """
        curl -X GET "%s?page=1&size=10" \\
        -H "%s" \\
        -H "%s"
        """.formatted(GET_ALL_PARTNERS_URL, CONTENT_TYPE, X_PARTNER_TOKEN);

    /**
     * JavaScript (Fetch) Example for getting all partners with pagination
     * This example shows how to use the Fetch API in JavaScript to send a GET request to the API.
     * The query parameters for pagination (`page=1&size=10`) are passed in the URL.
     */
    public static final String GET_ALL_PARTNERS_JS_FETCH = """
        fetch("%s?page=1&size=10", {
            method: "GET",
            headers: {
                "%s": "application/json",
                "%s": "%s"
            }
        })
        .then(response => response.json())
        .then(data => console.log(data));
        """.formatted(GET_ALL_PARTNERS_URL, CONTENT_TYPE, "X-Partner-Token", X_PARTNER_TOKEN);

    /**
     * Python (requests) Example for getting all partners with pagination
     * This example demonstrates how to send a GET request using the Python `requests` library with query parameters.
     * It includes pagination parameters (`page=1&size=10`) and prints the JSON response from the API.
     */
    public static final String GET_ALL_PARTNERS_PYTHON = """
        import requests
        
        url = "%s/list-partner"
        headers = {
            "Content-Type": "application/json",
            "X-Partner-Token": "%s"
        }
        params = {"page": 1, "size": 10}
        
        response = requests.get(url, params=params, headers=headers)
        print(response.json())
        """.formatted(GET_ALL_PARTNERS_URL, X_PARTNER_TOKEN);

    /**
     * Java (HttpURLConnection) Example for getting all partners with pagination
     * This example demonstrates how to send a GET request in Java using `HttpURLConnection`.
     * It passes pagination parameters (`page=1&size=10`) in the URL and prints the response.
     */
    public static final String GET_ALL_PARTNERS_JAVA_HTTPURLCONNECTION = """
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        
        public class Main {
            public static void main(String[] args) throws Exception {
                URL url = new URL("%s/list-partner?page=1&size=10");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("X-Partner-Token", "%s");
        
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
        
                System.out.println(response.toString());
            }
        }
        """.formatted(GET_ALL_PARTNERS_URL, X_PARTNER_TOKEN);

    /**
     * C# (HttpClient) Example for getting all partners with pagination
     * This example shows how to send a GET request in C# using `HttpClient` to fetch paginated data.
     * The pagination parameters are passed as query parameters in the URL (`page=1&size=10`).
     */
    public static final String GET_ALL_PARTNERS_CSHARP = """
        using System;
        using System.Net.Http;
        using System.Threading.Tasks;
        
        class Program
        {
            static async Task Main()
            {
                using HttpClient client = new HttpClient();
                string url = "%s/list-partner?page=1&size=10";
        
                client.DefaultRequestHeaders.Add("X-Partner-Token", "%s");
                client.DefaultRequestHeaders.Add("Content-Type", "application/json");
        
                HttpResponseMessage response = await client.GetAsync(url);
                string result = await response.Content.ReadAsStringAsync();
                Console.WriteLine(result);
            }
        }
        """.formatted(GET_ALL_PARTNERS_URL, X_PARTNER_TOKEN);

    /**
     * PHP (cURL) Example for getting all partners with pagination
     * This example demonstrates how to use PHP's `cURL` to send a GET request to fetch partners with pagination.
     * It adds query parameters (`page=1&size=10`) to the URL and prints the response.
     */
    public static final String GET_ALL_PARTNERS_PHP_CURL = """
        <?php
        $url = '%s/list-partner?page=1&size=10';
        $headers = [
            'Content-Type: application/json',
            'X-Partner-Token: %s'
        ];
        
        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        
        $response = curl_exec($ch);
        curl_close($ch);
        
        echo $response;
        ?>
        """.formatted(GET_ALL_PARTNERS_URL, X_PARTNER_TOKEN);

    /**
     * Node.js (Axios) Example for getting all partners with pagination
     * This example shows how to use `Axios` in Node.js to send a GET request with pagination parameters.
     * The `params` object contains `page=1` and `size=10`, which are passed in the URL for the request.
     */
    public static final String GET_ALL_PARTNERS_NODEJS = """
        const axios = require('axios');
        const url = '%s/list-partner';
        const params = { page: 1, size: 10 };
        
        axios.get(url, {
            params,
            headers: {
                'Content-Type': 'application/json',
                'X-Partner-Token': '%s'
            }
        })
        .then(response => console.log(response.data))
        .catch(error => console.error(error));
        """.formatted(GET_ALL_PARTNERS_URL, X_PARTNER_TOKEN);

    /**
     * Ruby (Net::HTTP) Example for getting all partners with pagination
     * This example demonstrates how to send a GET request in Ruby using `Net::HTTP`.
     * It includes query parameters (`page=1&size=10`) for pagination and prints the response body.
     */
    public static final String GET_ALL_PARTNERS_RUBY = """
        require 'net/http'
        require 'uri'
        require 'json'
        
        url = URI.parse('%s/list-partner?page=1&size=10')
        request = Net::HTTP::Get.new(url)
        request['Content-Type'] = 'application/json'
        request['X-Partner-Token'] = '%s'
        
        response = Net::HTTP.start(url.hostname, url.port, use_ssl: true) do |http|
            http.request(request)
        end
        
        puts response.body
        """.formatted(GET_ALL_PARTNERS_URL, X_PARTNER_TOKEN);

    /**
     * Go (net/http) Example for getting all partners with pagination
     * This example shows how to use the `net/http` package in Go to send a GET request with pagination.
     * The query parameters (`page=1&size=10`) are added to the URL, and the response status is printed.
     */
    public static final String GET_ALL_PARTNERS_GO = """
        package main
        
        import (
            "fmt"
            "net/http"
            "net/url"
        )
        
        func main() {
            baseURL := "%s/list-partner"
            params := url.Values{}
            params.Add("page", "1")
            params.Add("size", "10")
        
            req, _ := http.NewRequest("GET", baseURL+"?"+params.Encode(), nil)
            req.Header.Set("Content-Type", "application/json")
            req.Header.Set("X-Partner-Token", "%s")
        
            client := &http.Client{}
            resp, _ := client.Do(req)
            defer resp.Body.Close()
        
            fmt.Println("Response Status:", resp.Status)
        }
        """.formatted(GET_ALL_PARTNERS_URL, X_PARTNER_TOKEN);

    /**
     * Bank Statement JSON Example
     */
    public static final String BANK_STATEMENT_JSON = """
        {
            "Data": {
                "CMB_BANKSTM_STG": [
                    {
                        "CMB_BSP_STMT_DT": "2025-01-01",
                        "CMB_BANK_ACCOUNT_N": "String",
                        "CMB_CURRENCY_CD": "String",
                        "CMB_VALUE_DT": "2025-01-01",
                        "CMB_BANK_STMT_TYPE": "String",
                        "CMB_BSP_TRAN_AMT": 0.0,
                        "CMB_OPEN_BALANCE": 0.0,
                        "CMB_END_BALANCE": 0.0,
                        "CMB_IMMEDIATE_BAL": 0.0,
                        "CMB_RECON_REF_ID": "String",
                        "CMB_CHECK_NUMBER": "String",
                        "CMB_DESCRLONG": "String",
                        "CMB_LETTER_NUMBER": "String"
                    },
                    {
                        "CMB_BSP_STMT_DT": "2025-01-01",
                        "CMB_BANK_ACCOUNT_N": "String",
                        "CMB_CURRENCY_CD": "String",
                        "CMB_VALUE_DT": "2025-01-01",
                        "CMB_BANK_STMT_TYPE": "String",
                        "CMB_BSP_TRAN_AMT": 0.0,
                        "CMB_OPEN_BALANCE": 0.0,
                        "CMB_END_BALANCE": 0.0,
                        "CMB_IMMEDIATE_BAL": 0.0,
                        "CMB_RECON_REF_ID": "String",
                        "CMB_CHECK_NUMBER": "String",
                        "CMB_DESCRLONG": "String",
                        "CMB_LETTER_NUMBER": "String"
                    }
                ]
            }
        }
    """;

    /**
     * cURL Example for Import Bank Statement
     * Demonstrates how to send a POST request with bank statement JSON payload using cURL,
     * including headers required by CamDX and FMIS.
     */
    public static final String IMPORT_BANK_STATEMENT_CURL =
        "curl -X POST \"" + CAMDX_BANK_STATEMENT_URL + "\" \\\n" +
        "-H \"" + CONTENT_TYPE + "\" \\\n" +
        "-H \"" + X_ROAD_CLIENT + "\" \\\n" +
        "-H \"" + X_PARTNER_TOKEN + "\" \\\n" +
        "-u username:password \\\n" +
        "-d '" + BANK_STATEMENT_JSON + "'";

    /**
     * JavaScript (Fetch) Example for Import Bank Statement
     * This example shows how to use the Fetch API in JavaScript to send a POST request to import a bank statement.
     * It includes required headers for CamDX and FMIS, and the bank statement payload is passed in JSON format.
     */
    public static final String IMPORT_BANK_STATEMENT_JS_FETCH =
        "fetch('" + CAMDX_BANK_STATEMENT_URL + "', {\n" +
        "    method: 'POST',\n" +
        "    headers: {\n" +
        "        'Content-Type': '" + CONTENT_TYPE + "',\n" +
        "        'X-Road-Client': '" + X_ROAD_CLIENT + "',\n" +
        "        'X-Partner-Token': '" + X_PARTNER_TOKEN + "'\n" +
        "    },\n" +
        "    body: JSON.stringify(" + BANK_STATEMENT_JSON + ")\n" +
        "})\n" +
        ".then(response => response.json())\n" +
        ".then(data => console.log(data));";

    /**
     * Python (requests) Example for Import Bank Statement
     * This Python example uses the `requests` library to send a POST request for importing a bank statement.
     * It sends the required headers, such as `Content-Type` and `X-Partner-Token`, along with the JSON body containing the bank statement data.
     * The response is then printed out as a JSON object.
     */
    public static final String IMPORT_BANK_STATEMENT_PYTHON =
        "import requests\n\n" +
        "url = '" + CAMDX_BANK_STATEMENT_URL + "'\n" +
        "headers = {\n" +
        "    'Content-Type': '" + CONTENT_TYPE + "',\n" +
        "    'X-Road-Client': '" + X_ROAD_CLIENT + "',\n" +
        "    'X-Partner-Token': '" + X_PARTNER_TOKEN + "'\n" +
        "}\n" +
        "data = {\n" +
        "    'data': " + BANK_STATEMENT_JSON + "\n" +
        "}\n\n" +
        "response = requests.post(url, json=data, headers=headers)\n" +
        "print(response.json())";

    /**
     * Java (HttpURLConnection) Example for Import Bank Statement
     * This Java example demonstrates how to send a POST request using `HttpURLConnection` to import a bank statement.
     * The request includes headers such as `Content-Type` and `X-Partner-Token`, and the body contains JSON data for the bank statement.
     * The response is read and printed to the console.
     */
    public static final String IMPORT_BANK_STATEMENT_JAVA_HTTPURLCONNECTION =
        "import java.net.HttpURLConnection;\n" +
        "import java.net.URL;\n" +
        "import java.io.BufferedReader;\n" +
        "import java.io.InputStreamReader;\n\n" +
        "public class Main {\n" +
        "    public static void main(String[] args) throws Exception {\n" +
        "        String url = '" + CAMDX_BANK_STATEMENT_URL + "';\n" +
        "        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();\n" +
        "        con.setRequestMethod(\"POST\");\n" +
        "        con.setRequestProperty(\"Content-Type\", \"" + CONTENT_TYPE + "\");\n" +
        "        con.setRequestProperty(\"X-Road-Client\", \"" + X_ROAD_CLIENT + "\");\n" +
        "        con.setRequestProperty(\"X-Partner-Token\", \"" + X_PARTNER_TOKEN + "\");\n" +
        "        con.setDoOutput(true);\n\n" +
        "        String jsonData = '" + BANK_STATEMENT_JSON + "';\n\n" +
        "        con.getOutputStream().write(jsonData.getBytes(\"UTF-8\"));\n\n" +
        "        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));\n" +
        "        String inputLine;\n" +
        "        StringBuffer response = new StringBuffer();\n" +
        "        while ((inputLine = in.readLine()) != null) {\n" +
        "            response.append(inputLine);\n" +
        "        }\n" +
        "        in.close();\n\n" +
        "        System.out.println(response.toString());\n" +
        "    }\n" +
        "}";

    /**
     * C# (HttpClient) Example for Import Bank Statement
     * This C# example demonstrates how to use `HttpClient` to send a POST request to import a bank statement.
     * It includes the necessary headers (`Content-Type`, `X-Partner-Token`) and sends the data in JSON format.
     * The response is captured and printed as a string.
     */
    public static final String IMPORT_BANK_STATEMENT_CSHARP_HTTPCLIENT =
        "using System;\n" +
        "using System.Net.Http;\n" +
        "using System.Text;\n" +
        "using System.Threading.Tasks;\n" +
        "\n" +
        "class Program\n" +
        "{\n" +
        "    static async Task Main()\n" +
        "    {\n" +
        "        using HttpClient client = new HttpClient();\n" +
        "        string url = \"" + CAMDX_BANK_STATEMENT_URL + "\";\n" +
        "        client.DefaultRequestHeaders.Add(\"X-Partner-Token\", \"" + X_PARTNER_TOKEN + "\");\n" +
        "        client.DefaultRequestHeaders.Add(\"X-Road-Client\", \"" + X_ROAD_CLIENT + "\");\n" +
        "        var json = \"" + BANK_STATEMENT_JSON + "\";\n" +
        "\n" +
        "        var content = new StringContent(json, Encoding.UTF8, \"" + CONTENT_TYPE + "\");\n" +
        "        var response = await client.PostAsync(url, content);\n" +
        "        var result = await response.Content.ReadAsStringAsync();\n" +
        "        Console.WriteLine(result);\n" +
        "    }\n" +
        "}";

    /**
     * This PHP example uses cURL to send a POST request to import a bank statement.
     * It sets the necessary headers (`Content-Type` and `X-Partner-Token`) and sends the JSON data containing bank statement information.
     * The response from the server is printed out.
     */
    /**
     * This PHP example uses cURL to send a POST request to import a bank statement.
     * It sets the necessary headers (`Content-Type`, `X-Partner-Token`, and `X-Road-Client`)
     * and sends the JSON data containing bank statement information.
     * The response from the server is printed out.
     */
    public static final String IMPORT_BANK_STATEMENT_PHP_CURL = "<?php\n" +
        "    $url = '" + CAMDX_BANK_STATEMENT_URL + "';\n" +
        "    $headers = [\n" +
        "        'Content-Type: " + CONTENT_TYPE + "',\n" +
        "        'X-Partner-Token: " + X_PARTNER_TOKEN + "',\n" +
        "        'X-Road-Client: " + X_ROAD_CLIENT + "'\n" +
        "    ];\n" +
        "    $data = '" + BANK_STATEMENT_JSON + "';\n" +
        "\n" +
        "    $ch = curl_init($url);\n" +
        "    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);\n" +
        "    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);\n" +
        "    curl_setopt($ch, CURLOPT_POST, true);\n" +
        "    curl_setopt($ch, CURLOPT_POSTFIELDS, $data);\n" +
        "\n" +
        "    $response = curl_exec($ch);\n" +
        "    curl_close($ch);\n" +
        "\n" +
        "    echo $response;\n" +
        "?>";

    /**
     * This Node.js example uses Axios to send a POST request to import a bank statement.
     * It includes necessary headers (`Content-Type`, `X-Partner-Token`, and `X-Road-Client`),
     * and sends the data as JSON format.
     * The response data is logged to the console.
     */
    public static final String IMPORT_BANK_STATEMENT_NODEJS =
        "const axios = require('axios');\n" +
        "const url = '" + CAMDX_BANK_STATEMENT_URL + "';\n" +
        "const headers = {\n" +
        "    'Content-Type': '" + CONTENT_TYPE + "',\n" +
        "    'X-Partner-Token': '" + X_PARTNER_TOKEN + "',\n" +
        "    'X-Road-Client': '" + X_ROAD_CLIENT + "'\n" +
        "};\n" +
        "const data = " + BANK_STATEMENT_JSON + ";\n" +
        "\n" +
        "axios.post(url, data, { headers })\n" +
        "    .then(response => console.log(response.data))\n" +
        "    .catch(error => console.error(error));";

    /**
     * This Ruby example uses Net::HTTP to send a POST request to import a bank statement.
     * It includes headers (`Content-Type`, `X-Partner-Token`, and `X-Road-Client`) and sends the data as JSON format.
     * The response body is printed out after the request completes.
     */
    public static final String IMPORT_BANK_STATEMENT_RUBY =
        "require 'net/http'\n" +
        "require 'uri'\n" +
        "require 'json'\n" +
        "\n" +
        "url = URI.parse('" + CAMDX_BANK_STATEMENT_URL + "')\n" +
        "header = {'Content-Type' => '" + CONTENT_TYPE + "', 'X-Partner-Token' => '" + X_PARTNER_TOKEN + "', 'X-Road-Client' => '" + X_ROAD_CLIENT + "'}\n" +
        "data = " + BANK_STATEMENT_JSON + "\n" +
        "\n" +
        "uri = URI.parse(url)\n" +
        "request = Net::HTTP::Post.new(uri, header)\n" +
        "request.body = data\n" +
        "\n" +
        "response = Net::HTTP.start(uri.hostname, uri.port) { |http| http.request(request) }\n" +
        "puts response.body";

    /**
     * This Go example uses the net/http package to send a POST request to import a bank statement.
     * The headers (`Content-Type`, `X-Partner-Token`, and `X-Road-Client`) and data are set in the request,
     * and the response status is printed out.
     */
    public static final String IMPORT_BANK_STATEMENT_GO =
        "package main\n\n" +
        "import (\n" +
        "    \"bytes\"\n" +
        "    \"fmt\"\n" +
        "    \"net/http\"\n" +
        "    \"log\"\n" +
        ")\n\n" +
        "func main() {\n" +
        "    url := \"" + CAMDX_BANK_STATEMENT_URL + "\"\n" +
        "    jsonData := []byte(" + BANK_STATEMENT_JSON + ")\n" +
        "\n" +
        "    req, err := http.NewRequest(\"POST\", url, bytes.NewBuffer(jsonData))\n" +
        "    if err != nil {\n" +
        "        log.Fatal(err)\n" +
        "    }\n\n" +
        "    req.Header.Set(\"Content-Type\", \"" + CONTENT_TYPE + "\")\n" +
        "    req.Header.Set(\"X-Partner-Token\", \"" + X_PARTNER_TOKEN + "\")\n" +
        "    req.Header.Set(\"X-Road-Client\", \"" + X_ROAD_CLIENT + "\")\n" +
        "\n" +
        "    client := &http.Client{}\n" +
        "    resp, err := client.Do(req)\n" +
        "    if err != nil {\n" +
        "        log.Fatal(err)\n" +
        "    }\n" +
        "    defer resp.Body.Close()\n\n" +
        "    fmt.Println(\"Response status:\", resp.Status)\n" +
        "}\n";

    /**
     * Fetches the example for a given endpoint.
     *
     * @param endpoint the API endpoint
     * @return the example as a JSON string, or "{}" if no example is found
     */
    public static String getExample(String endpoint) {
        return switch (endpoint) {
            case "/api/v1/create-partner" -> PARTNER_JSON;
            case "/api/v1/list-partner" -> GET_ALL_PARTNERS_CURL;
            default -> "{}";
        };
    }
}