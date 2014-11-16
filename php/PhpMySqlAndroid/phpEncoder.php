<?php
if (NULL != filter_input(INPUT_GET, 'actionEncode')) {
  if (filter_input(INPUT_GET, 'actionEncode') == 'encodePassword') {

    $str = filter_input(INPUT_GET, 'password');
    print base64_encode(base64_encode($str));
  }
}

//http://192.168.0.215/PhpMySqlAndroid/phpEncoder.php?actionEncode=encodePassword&password=iw3072ylB
?>
