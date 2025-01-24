export interface PreferenceTitle {
  requiredRank: number,
  index: number,
  title: string
}

export function findTitle(index: number): PreferenceTitle {
  return preferenceArray.find(pref => pref.index === index)!;
}

export const preferenceArray: PreferenceTitle[] = [
  {requiredRank: 0, index: 0, title: 'Mortal'},
  {requiredRank: 0, index: 1, title: 'Insignificant Ant'},
  {requiredRank: 0, index: 2, title: 'Speck of dust'},
  {requiredRank: 0, index: 3, title: 'Old librarian'},
  {requiredRank: 0, index: 4, title: 'Cultivation vessel'},

  {requiredRank: 1, index: 10, title: 'Junior disciple'},
  {requiredRank: 1, index: 11, title: 'Ant'},
  {requiredRank: 1, index: 12, title: 'Servant disciple'},
  {requiredRank: 1, index: 13, title: 'Junior'},
  {requiredRank: 1, index: 14, title: 'Young Master\'s lackey B'},

  {requiredRank: 2, index: 20, title: 'Senior disciple'},
  {requiredRank: 2, index: 21, title: 'Outer court disciple'},
  {requiredRank: 2, index: 22, title: 'Inner court disciple'},
  {requiredRank: 2, index: 23, title: 'Young Master\'s lackey A'},
  {requiredRank: 2, index: 24, title: 'Slightly bigger ant'},
  {requiredRank: 2, index: 25, title: 'Lone cultivator'},
  {requiredRank: 2, index: 26, title: 'Jade beauty'},

  {requiredRank: 3, index: 30, title: 'Expert'},
  {requiredRank: 3, index: 31, title: 'Legacy disciple'},
  {requiredRank: 3, index: 32, title: 'Young Master'},
  {requiredRank: 3, index: 33, title: 'Sect uncle'},
  {requiredRank: 3, index: 34, title: 'Outer court Elder'},
  {requiredRank: 3, index: 35, title: 'Refinement master'},
  {requiredRank: 3, index: 36, title: 'Formation master'},
  {requiredRank: 3, index: 37, title: 'Alchemy master'},

  {requiredRank: 4, index: 40, title: 'Eccentric'},
  {requiredRank: 4, index: 41, title: 'Inner court elder'},
  {requiredRank: 4, index: 42, title: 'Young Master\'s father'},
  {requiredRank: 4, index: 43, title: 'Sect leader'},
  {requiredRank: 4, index: 44, title: 'Fairy'},
  {requiredRank: 4, index: 45, title: 'Genius'},
  {requiredRank: 4, index: 46, title: 'Old man'},
  {requiredRank: 4, index: 47, title: 'Granny'},

  {requiredRank: 5, index: 50, title: 'Lord'},
  {requiredRank: 5, index: 51, title: 'Righteous'},
  {requiredRank: 5, index: 52, title: 'Demon'},
  {requiredRank: 5, index: 53, title: 'Young Master\'s grandfather'},
  {requiredRank: 5, index: 54, title: 'Inheritor'},
  {requiredRank: 5, index: 55, title: 'True prodigy'},

  {requiredRank: 6, index: 60, title: 'Old monster'},
  {requiredRank: 6, index: 61, title: 'Saint'},
  {requiredRank: 6, index: 62, title: 'Shackle breaking'},
  {requiredRank: 6, index: 63, title: 'Ancestor'},
  {requiredRank: 6, index: 64, title: 'Supreme elder'},
  {requiredRank: 6, index: 65, title: 'Timeless genius'},

  {requiredRank: 7, index: 70, title: 'Secluded'},
  {requiredRank: 7, index: 71, title: 'Fanatic'},
  {requiredRank: 7, index: 72, title: 'Famed'},
  {requiredRank: 7, index: 73, title: 'Fabled'},
  {requiredRank: 7, index: 74, title: 'Hidden'},
  {requiredRank: 7, index: 75, title: 'Mount Tai'},

  {requiredRank: 8, index: 80, title: 'Immortal'},
  {requiredRank: 8, index: 81, title: 'Exalted elder'},
  {requiredRank: 8, index: 82, title: 'Ancient'},
  {requiredRank: 8, index: 83, title: 'Ever-burning'},
  {requiredRank: 8, index: 84, title: 'Sky above the sky'},
  {requiredRank: 8, index: 85, title: 'Heavenly'},
  {requiredRank: 8, index: 86, title: 'Lone immortal'},

  {requiredRank: 9, index: 90, title: 'Pilgrim'},
  {requiredRank: 9, index: 91, title: 'Grain of sand'},
  {requiredRank: 9, index: 92, title: 'Beggar'},
  {requiredRank: 9, index: 93, title: 'Unperturbed'},
  {requiredRank: 9, index: 94, title: 'Founding ancestor'},
  {requiredRank: 9, index: 95, title: 'Forgotten'},
  {requiredRank: 9, index: 96, title: 'The'},

  {requiredRank: 10, index: 100, title: 'Ascendant'},
  {requiredRank: 10, index: 101, title: 'Dao Lord'},
  {requiredRank: 10, index: 102, title: 'Venerable'},
  {requiredRank: 10, index: 103, title: 'Forever waiting'},
];
export const rankArray: string[] = ['Mortal', 'Qi condensation', 'Foundation establishment', 'Core formation', 'Nascent soul', 'Spirit transformation', 'Spirit severing', 'Vessel refining', 'Eternal furnace', 'Dao seeking', 'Ascension'];
export const progressArray: number[] = [3, 5, 10, 20, 40, 60, 100, 200, 500, 1000];
