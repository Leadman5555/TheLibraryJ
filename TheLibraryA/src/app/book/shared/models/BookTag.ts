export type BookTag =
  'XIANXIA'
  | 'WUXIA'
  | 'COMEDY'
  | 'DRAMA'
  | 'ACTION'
  | 'FANTASY'
  | 'MYSTERY'
  | 'HISTORICAL'
  | 'UNTAGGED';
export const allTags: BookTag[] = ['XIANXIA', 'WUXIA', 'HISTORICAL', 'DRAMA', 'ACTION', 'FANTASY', 'MYSTERY', 'COMEDY', 'UNTAGGED'];
export const allTagsAsString: string[] = allTags.map(tag => tag.toString());
