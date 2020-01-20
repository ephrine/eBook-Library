import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';

class BrowseStore extends StatefulWidget {
  BrowseStore({Key key}) : super(key: key);

  @override
  _BrowseStore createState() => _BrowseStore();
}

class _BrowseStore extends State<BrowseStore> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Container(
        child: const WebView(
          initialUrl: 'https://www.google.con',
          javascriptMode: JavascriptMode.unrestricted,
        ),
      ),
    );
  }
}