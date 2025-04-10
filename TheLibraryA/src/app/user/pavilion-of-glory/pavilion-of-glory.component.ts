import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserProfileService} from '@app/user/profile/user-profile.service';
import {TopRankerResponse} from '@app/user/shared/models/top-ranker-response';
import {findTitle, rankArray} from '@app/user/profile/shared/rankTitles';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-pavilion-of-glory',
  imports: [
    RouterLink
  ],
  templateUrl: './pavilion-of-glory.component.html',
  styleUrl: './pavilion-of-glory.component.css',
  standalone: true
})
export class PavilionOfGloryComponent implements OnInit, OnDestroy {

  constructor(private userProfileService: UserProfileService) {
  }

  showRanking: boolean = false;
  topRankerList?: TopRankerResponse[];
  fetchingErrorMessage?: string;

  ngOnInit(): void {
    this.userProfileService.fetchTopRankingUsers().subscribe({
      next: (v) => {
        this.audio.loop = true;
        this.topRankerList = v;
      },
      error: (err: string) => this.fetchingErrorMessage = err
    });
  }

  openTheRanking(){
    this.playLoadSound();
    setTimeout(() => {this.showRanking = true;}, 1000);
  }

  private readonly pathToSoundFile = '/sounds/Reverie Millenary  by_ PoKeR.mp3';
  private readonly audio = new Audio(this.pathToSoundFile);

  private async playLoadSound(){
    await this.audio.play();
  }

  ngOnDestroy(): void {
    this.audio.pause();
  }

  protected readonly findTitle = findTitle;
  protected readonly rankArray = rankArray;
}
