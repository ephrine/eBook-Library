import 'package:flutter/material.dart';

class Settings extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Container(
        child: Center(
          child: Column(
            // center the children
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Icon(
                Icons.favorite,
                size: 160.0,
                color: Colors.white,
              ),
              Text(
                "3 Tab",
                style: TextStyle(color: Colors.white),
              ),
              RaisedButton(
                  onPressed: () {
                    // Validate will return true if the form is valid, or false if
                    // the form is invalid.
                  },
                  child: Text("Login"))
            ],
          ),
        ),
      ),
    );
  }
}
