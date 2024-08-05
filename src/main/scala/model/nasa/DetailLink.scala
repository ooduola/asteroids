package model.nasa

import io.circe._
import io.circe.generic.semiauto._

case class DetailLink(self: String)

object DetailLink {
  implicit val detailLinkCodec: Codec.AsObject[DetailLink] = deriveCodec
}
