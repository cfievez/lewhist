import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClient, HttpClientModule } from "@angular/common/http";

import { AppComponent } from './app.component';
import { ApiService } from "./config/api.service";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatButtonModule } from "@angular/material/button";
import { MatInputModule } from "@angular/material/input";
import { FormsModule } from "@angular/forms";
import { BoardComponent, JokerCardDialog, TwoCardDialog } from "./board/board.component";
import { MatTableModule } from "@angular/material/table";
import { MatDialogModule } from "@angular/material/dialog";
import { InjectableRxStompConfig, RxStompService, rxStompServiceFactory } from "@stomp/ng2-stompjs";
import { myRxStompConfig } from "./my-rx-stomp.config";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";

@NgModule({
  declarations: [
    AppComponent,
    BoardComponent,
    TwoCardDialog,
    JokerCardDialog,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    MatInputModule,
    FormsModule,
    MatTableModule,
    MatDialogModule,
    MatSnackBarModule
  ],
  providers: [
    HttpClient,
    ApiService,
    {
      provide: InjectableRxStompConfig,
      useValue: myRxStompConfig,
    },
    {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
      deps: [InjectableRxStompConfig],
    },
  ],
  bootstrap: [AppComponent],
  entryComponents: [
    TwoCardDialog,
    JokerCardDialog,
  ]
})
export class AppModule { }
