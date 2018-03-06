package com.example.receiptdecoratorservice;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.poynt.os.model.PrintedReceipt;
import co.poynt.os.model.PrintedReceiptLine;
import co.poynt.os.model.PrintedReceiptSection;
import co.poynt.os.services.v1.IPoyntReceiptDecoratorService;
import co.poynt.os.services.v1.IPoyntReceiptDecoratorServiceListener;

public class ReceiptDecoratorService extends Service {
    public ReceiptDecoratorService() {
        EMPTY_LINE = new PrintedReceiptLine();
        EMPTY_LINE.setText("\n");
    }

    private static final String TAG = ReceiptDecoratorService.class.getSimpleName();

    private final PrintedReceiptLine EMPTY_LINE;
    private IPoyntReceiptDecoratorService.Stub mBinder = new IPoyntReceiptDecoratorService.Stub() {

        // Support for legacy PrintedReceipt
        @Override
        public void decorate(PrintedReceipt printedReceipt, String requestId, IPoyntReceiptDecoratorServiceListener
                listener) throws RemoteException {
            List<PrintedReceiptLine> footerLines = printedReceipt.getFooter();
            updateReceipt(footerLines);
            printedReceipt.setFooter(footerLines);
            listener.onReceiptDecorated(requestId, printedReceipt);
        }

        @Override
        public void cancel(String requestId) throws RemoteException {

        }

        @Override
        public void decorateV2(co.poynt.os.model.PrintedReceiptV2 printedReceiptV2,
                               String requestId, co.poynt.os.services.v1.IPoyntReceiptDecoratorListener listener) throws RemoteException {
            PrintedReceiptSection footerSection = printedReceiptV2.getFooter();
            List<PrintedReceiptLine> footerLines = footerSection.getLines();

            footerLines = updateReceipt(footerLines);

            footerSection.setLines(footerLines);
            printedReceiptV2.setFooter(footerSection);
            listener.onReceiptDecorated(requestId, printedReceiptV2);
        }
    };

    @NonNull
    private List<PrintedReceiptLine> updateReceipt(List<PrintedReceiptLine> footerLines) {
        if (footerLines == null) {
            footerLines = new ArrayList<>();
        }

        for (PrintedReceiptLine line : footerLines) {
            Log.d(TAG, "decorate: " + line.getText());
        }
        EMPTY_LINE.setText("\n");

        PrintedReceiptLine customText = new PrintedReceiptLine();
        customText.setText("Follow us on Facebook");

        footerLines.add(EMPTY_LINE);
        footerLines.add(customText);
        footerLines.add(EMPTY_LINE);
        footerLines.add(EMPTY_LINE);
        footerLines.add(EMPTY_LINE);
        return footerLines;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
