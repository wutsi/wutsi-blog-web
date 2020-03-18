function Wutsi (){

    this.error = function (msg, details){
        console.log('ERROR', msg, details);
        alert(msg);
    };

    this.warn = function (msg, details){
        console.log('WARNING', msg, details)
    };

    this.subscribe = function (formData, callback) {
        console.log('Performing payment...');

        var url = '/settings/subscription/subscribe';
        $.ajax({

            url: url,
            data: formData,
            method: 'POST'

        }).done(function(data) {

            console.log('Payment completed', data);
            if (callback) {
                callback(data);
            }

        }).fail(function(xhr, textStatus, errorThrown){
            console.log('Failed - ' + url, xhr, textStatus, errorThrown);
            if (callback){
                callback({
                    transactionId: -1,
                    status: 'failed',
                    error: 'internal_processing_error',
                    errorText: 'Internal error has occured'
                });
            }
        });
    };

    this.waitForPaymentConfirmation = function(tx, callback){
        var handle = -1;
        var retry = 0;
        var worker = function(){

            console.log(retry + '. Requesting status of Transaction#' + tx.id);
            retry++;
            var url = '/settings/subscription/status?transactionId=' + tx.id + '&retry=' + retry;
            $.ajax({
                url: url
            }).done(function(transaction) {

                console.log(retry + '. Transaction#' + tx.id, transaction);
                if (transaction.status != 'pending'){
                    clearInterval(handle);
                    if (callback){
                        callback(transaction)
                    }
                }

            }).fail(function(jqXHR, textStatus, errorThrown){
                console.log('Failed - ' + url, jqXHR, textStatus, errorThrown);
                if (retry > 2) {
                    clearInterval(handle);
                    if (callback){
                        callback({
                            transactionId: -1,
                            status: 'failed',
                            error: 'internal_processing_error',
                            errorText: 'Internal error has occured'
                        });
                    }
                }
            });

        };

        callback({
            status: 'success'
        })
    };
}

var wutsi = new Wutsi();
