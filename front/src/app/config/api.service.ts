import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import { GameViewApiDto } from "./dto/gameView.api.dto";

@Injectable()
export class ApiService {

  constructor(private http: HttpClient) { }

  public getWhist(): Observable<GameViewApiDto> {
    return this.http.get<GameViewApiDto>('rest/whist');
  }

  public login(username): Observable<any> {
    return this.http.post(`rest/login?username=${username}`, null);
  }

  public join(): Observable<any> {
    return this.http.post(`rest/join`, null);
  }

  public start(): Observable<any> {
    return this.http.post(`rest/start`, null);
  }

  public talk(contract: number): Observable<any> {
    return this.http.post(`rest/contract?value=${contract}`, null);
  }

  public play(card: string): Observable<any> {
    return this.http.post(`rest/play?card=${card}`, null);
  }

  public playWithBonus(card: string, bonus: string): Observable<any> {
    return this.http.post(`rest/play?card=${card}&bonus=${bonus}`, null);
  }

  public voteToEndTrick(): Observable<any> {
    return this.http.post(`rest/voteToEndTrick`, null);
  }

  public giveTrickToPlayer(targetedPlayer: string): Observable<any> {
    return this.http.post(`rest/distribute?targetedPlayer=${targetedPlayer}`, null);
  }

}

