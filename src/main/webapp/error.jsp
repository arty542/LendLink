<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        .error-container {
            margin-top: 50px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="alert alert-danger error-container" role="alert">
                    <h4 class="alert-heading">An Error Occurred!</h4>
                    <p>${requestScope.error}</p>
                    <hr>
                    <p class="mb-0">Please try navigating back or contact support if the problem persists.</p>
                </div>
            </div>
        </div>
    </div>
</body>
</html> 