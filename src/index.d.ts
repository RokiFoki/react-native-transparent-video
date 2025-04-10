import * as React from 'react';
import { StyleProp, ViewStyle } from 'react-native';

export interface TransparentVideoProps {
  style: StyleProp<ViewStyle>;
  source?: any;
  loop?: boolean;
  autoplay?: boolean;
}

declare class TransparentVideo extends React.PureComponent<TransparentVideoProps> {}

export default TransparentVideo;
