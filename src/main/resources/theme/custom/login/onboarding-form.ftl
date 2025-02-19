<#import "template.ftl" as layout>

<@layout.registrationLayout displayMessage=true; section>
    <h1>Convonest Analytics</h1>

    <form action="${url.loginAction}" method="post">
        <div class="form-group">
            <label for="phone"><strong>Phone Number</strong></label>
            <input type="text" id="phone" name="phone" class="form-control" required>
        </div>

        <div class="form-group">
            <label for="company"><strong>Company Name</strong></label>
            <input type="text" id="company" name="company" class="form-control" required>
        </div>

        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
</@layout.registrationLayout>
