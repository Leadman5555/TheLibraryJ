export type BookTag = 'UNTAGGED' | 'TAG1' | 'TAG2'| 'TAG3'| 'TAG4'| 'TAG5'| 'TAG6'| 'TAG7'| 'TAG8';
export const allTags: BookTag[] = ['TAG1', 'TAG2', 'TAG3', 'TAG4', 'TAG5', 'TAG6', 'TAG7', 'TAG8', 'UNTAGGED'];
export const allTagsAsString: string[] = allTags.map(tag => tag.toString());
export function identifyTag(_: number, item: BookTag) {
  return item;
}
