import { WhistApiDto } from "./whist.api.dto";

export interface GameViewApiDto {

  status: PlayerStatus;

  whist: WhistApiDto;

}

export type PlayerStatus = 'UNAUTHENTICATED' | 'SPECTATING' | 'WAITING_TO_PLAY' | 'PLAYING';
